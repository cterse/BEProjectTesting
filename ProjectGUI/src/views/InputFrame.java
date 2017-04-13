package views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FileChooserUI;

import common.sentenceSplitting.FormSentences;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class InputFrame extends JFrame {

	private JPanel contentPane;
	private JTextArea txtrInput;
	private JTextArea txtrSentences;
	private JButton btnImportFile;
	private JButton btnClearTextArea;
	private JButton btnGo;
	private JButton btnNext;
	private JScrollPane inputScrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InputFrame frame = new InputFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InputFrame() {
		setTitle("Class Diagram Using NLP");
		initComponents();
		createEvents();
		
	}

	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 526, 485);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblInputFile = new JLabel("Input File");
		
		txtrInput = new JTextArea();
		txtrInput.setWrapStyleWord(true);
		txtrInput.setLineWrap(true);
		
		JLabel lblSentences = new JLabel("Sentences");
		
		txtrSentences = new JTextArea();
		txtrSentences.setWrapStyleWord(true);
		txtrSentences.setLineWrap(true);
		
		btnImportFile = new JButton("Import File");
		
		btnClearTextArea = new JButton("Clear Text Area");
		
		btnGo = new JButton("Go");
		
		btnNext = new JButton("Next");
		
		inputScrollPane = new JScrollPane(txtrInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblSentences)
							.addContainerGap(440, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(txtrSentences)
								.addComponent(lblInputFile)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(txtrInput)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(inputScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addGap(50)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnNext, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnGo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnImportFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnClearTextArea, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(35))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(lblInputFile)
									.addGap(11)
									.addComponent(txtrInput, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(57)
									.addComponent(btnImportFile)
									.addGap(18)
									.addComponent(btnClearTextArea)
									.addPreferredGap(ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
									.addComponent(btnGo)))
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblSentences)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(txtrSentences, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
								.addComponent(btnNext)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(129)
							.addComponent(inputScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setAutoCreateGaps(true);
		gl_contentPane.setAutoCreateContainerGaps(true);
		contentPane.setLayout(gl_contentPane);
		
	}

	private void createEvents() {
		btnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(null);
				File inputFile = fileChooser.getSelectedFile();
				Scanner t = null;
				try {
					t = new Scanner(inputFile);
					String temp = "";
					while(t.hasNext()) {
						temp += t.next() + " ";
					}
					txtrInput.setText(temp);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnClearTextArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtrInput.setText("");
			}
		});
		
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<String> sentences = FormSentences.getSentences(txtrInput.getText());
					for(int i=0; i<sentences.size(); i++) {
						txtrSentences.append(sentences.get(i)+"\n");
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
}
