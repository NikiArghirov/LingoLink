/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package nea.coursework.lingolink;

import com.LingoLink.dao.MistakesDAO;
import com.LingoLink.dao.ProgressDAO;
import com.LingoLink.dao.Question;
import com.LingoLink.dao.QuestionDAO;
import java.util.List;

public class unitTest extends javax.swing.JPanel {
    private final loginScreen loginPanel;
    private int currentUnitId = -1;
    private int currentUserId = -1;
    private ProgressDAO progressDAO = new ProgressDAO();
    private MistakesDAO mistakesDAO = new MistakesDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private List<Question> currentQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions = 0;
    private String unitName = "";

    public unitTest(loginScreen login) {
        initComponents();
        this.loginPanel = login;
        
        if (!progressDAO.checkProgressTableExists()) {
            System.out.println("Progress table doesn't exist, creating it...");
            progressDAO.createProgressTableIfNotExists();
        }
        
        if (!mistakesDAO.checkMistakesTableExists()) {
            System.out.println("Mistakes table doesn't exist, creating it...");
            mistakesDAO.createMistakesTableIfNotExists();
        }
    }

    public void setUnitId(int unitId) {
        this.currentUnitId = unitId;
        this.currentQuestionIndex = 0;
        this.score = 0;

        if (loginPanel != null) {
            this.currentUserId = loginPanel.getCurrentUserId();
            System.out.println("UnitTest: Retrieved User ID from login: " + currentUserId);
        } else {
            System.out.println("UnitTest: WARNING - loginPanel is null!");
        }

        if (currentUserId <= 0) {
            System.out.println("UnitTest: WARNING - No valid user ID. Using default ID 1.");
            currentUserId = 1;
        }

        this.unitName = questionDAO.getUnitNameById(unitId);
        jLabel1.setText(unitName + " Test");
        loadQuestionsForUnit();
        displayCurrentQuestion();
    }

    public void setUnitId(int unitId, int userId) {
        this.currentUnitId = unitId;
        this.currentUserId = userId;
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.unitName = questionDAO.getUnitNameById(unitId);
        jLabel1.setText(unitName + " Test");
        loadQuestionsForUnit();
        displayCurrentQuestion();
    }

    private void loadQuestionsForUnit() {
        try {
            currentQuestions = questionDAO.getQuestionsByUnitId(currentUnitId);
            if (currentQuestions != null && !currentQuestions.isEmpty()) {
                totalQuestions = currentQuestions.size();
                AnswerField.setText("");
                AnswerField.setEnabled(true);
                System.out.println("Loaded " + totalQuestions + " questions for Unit " + currentUnitId);
            } else {
                QuestionField.setText("No questions available for this unit.");
                AnswerField.setEnabled(false);
                AnswerField.setText("No questions available");
                jButton2.setText("Finish Test");
                totalQuestions = 0;
                System.out.println("No questions found for Unit " + currentUnitId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            QuestionField.setText("Error loading questions: " + e.getMessage());
            AnswerField.setEnabled(false);
            jButton2.setEnabled(false);
        }
    }

    private void displayCurrentQuestion() {
        if (currentQuestions != null && currentQuestionIndex < currentQuestions.size()) {
            Question currentQuestion = currentQuestions.get(currentQuestionIndex);
            QuestionField.setText(currentQuestion.getText());
            AnswerField.setText("");
            AnswerField.setEnabled(true);

            if (currentQuestionIndex == currentQuestions.size() - 1) {
                jButton2.setText("Finish Test");
                System.out.println("Displaying last question #" + (currentQuestionIndex + 1));
            } else {
                jButton2.setText("Next question");
                System.out.println("Displaying question #" + (currentQuestionIndex + 1) + " of " + totalQuestions);
            }
        } else {
            showTestSummary();
        }
    }

    private void checkAnswerAndMoveNext() {
        System.out.println("checkAnswerAndMoveNext called - Question #" + (currentQuestionIndex + 1));

        if (currentQuestions != null && currentQuestionIndex < currentQuestions.size()) {
            Question currentQuestion = currentQuestions.get(currentQuestionIndex);
            String userAnswer = AnswerField.getText().trim();

            boolean isCorrect = questionDAO.checkAnswer(
                    currentQuestion.getQuestionId(),
                    userAnswer
            );

            if (isCorrect) {
                score++;
                System.out.println("Answer correct! Score: " + score);
            } else {
                System.out.println("Answer incorrect. Score remains: " + score);
                
                if (currentUserId > 0) {
                    String correctAnswer = currentQuestion.getCorrectAnswer();
                    String questionText = currentQuestion.getText();

                    System.out.println("Saving mistake for UserID: " + currentUserId);
                    
                    boolean saved = mistakesDAO.saveMistake(
                        currentUserId,
                        currentQuestion.getQuestionId(),
                        userAnswer.isEmpty() ? "(blank)" : userAnswer,
                        correctAnswer,
                        questionText
                    );

                    if (saved) {
                        System.out.println("Mistake saved to database");
                    } else {
                        System.out.println("Failed to save mistake to database");
                    }
                }
            }

            currentQuestionIndex++;
            displayCurrentQuestion();
        } else {
            showTestSummary();
        }
    }

    private void showTestSummary() {
        try {
            int percentage = calculatePercentage();
            System.out.println("=== Test Results ===");
            System.out.println("User ID: " + currentUserId);
            System.out.println("Unit ID: " + currentUnitId);
            System.out.println("Unit Name: " + unitName);
            System.out.println("Score: " + score + "/" + totalQuestions);
            System.out.println("Percentage: " + percentage + "%");

            boolean saved = false;
            if (currentUserId > 0) {
                saved = progressDAO.saveHighestScore(currentUserId, currentUnitId, percentage);
                System.out.println("Saved to database: " + (saved ? "Yes" : "No"));

                int previousScore = progressDAO.getProgressScore(currentUserId, currentUnitId);
                if (previousScore != -1) {
                    System.out.println("Previous best: " + previousScore + "%");
                }
            } else {
                System.out.println("WARNING: Not saved - invalid user ID (" + currentUserId + ")");
            }

            testSummary summaryPanel = (testSummary) loginPanel.findPanel("testSummary");
            if (summaryPanel != null) {
                summaryPanel.setTestResults(score, totalQuestions, unitName);
                loginPanel.showPanel("testSummary");
            } else {
                String savedText;
                if (currentUserId <= 0) {
                    savedText = "⚠ Test not saved (no user logged in)";
                } else if (saved) {
                    savedText = "✓ Saved to database";
                } else {
                    int previousScore = progressDAO.getProgressScore(currentUserId, currentUnitId);
                    savedText = (previousScore >= percentage)
                            ? "ℹ Score not saved (previous score was higher)"
                            : "✗ Not saved";
                }

                QuestionField.setText("Test completed!\n\n"
                        + "Unit: " + unitName + "\n"
                        + "Score: " + score + "/" + totalQuestions + "\n"
                        + "Percentage: " + percentage + "%\n\n"
                        + savedText);
                AnswerField.setEnabled(false);
                AnswerField.setText("Test completed!");
                jButton2.setEnabled(false);
                jButton2.setText("Test Completed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            QuestionField.setText("Test completed! Score: " + score + "/" + totalQuestions);
            AnswerField.setEnabled(false);
            jButton2.setEnabled(false);
        }
    }

    private int calculatePercentage() {
        if (totalQuestions == 0) {
            return 0;
        }
        return (int) Math.round((score * 100.0) / totalQuestions);
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("UnitTest: User ID manually set to " + userId);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        QuestionField = new javax.swing.JTextPane();
        AnswerField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBackground(new java.awt.Color(193, 230, 223));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jButton1.setBackground(new java.awt.Color(242, 242, 242));
        jButton1.setText("Quit test");
        jButton1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jButton1.setPreferredSize(new java.awt.Dimension(0, 0));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jButton1, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Unit Test");
        jLabel1.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("[Logo]");
        jLabel2.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        add(jPanel1, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(193, 230, 223));
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 228, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        jPanel3.setBackground(new java.awt.Color(193, 230, 223));
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 228, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        add(jPanel3, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(193, 230, 223));
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel7.setBackground(new java.awt.Color(193, 230, 223));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel7, gridBagConstraints);

        jButton2.setBackground(new java.awt.Color(242, 242, 242));
        jButton2.setText("Next question");
        jButton2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jButton2.setPreferredSize(new java.awt.Dimension(0, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        add(jPanel4, gridBagConstraints);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jPanel5.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        QuestionField.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jScrollPane1.setViewportView(QuestionField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane1, gridBagConstraints);

        AnswerField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        AnswerField.setText("Enter answer");
        AnswerField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnswerFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(AnswerField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel5, gridBagConstraints);

        jPanel6.setBackground(new java.awt.Color(193, 230, 223));
        jPanel6.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 642, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(jPanel6, gridBagConstraints);

        jPanel8.setBackground(new java.awt.Color(193, 230, 223));
        jPanel8.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 642, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(jPanel8, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        loginPanel.showPanel("progress");
        jButton2.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void AnswerFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnswerFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AnswerFieldActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        checkAnswerAndMoveNext();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AnswerField;
    private javax.swing.JTextPane QuestionField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
