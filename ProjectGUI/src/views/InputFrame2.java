package views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import java.awt.Rectangle;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class InputFrame2 extends JFrame {

	JButton btnImportFile;
	JButton btnClearInputField;
	JButton btnGetSentences;
	JButton btnNext;
	JTextArea txtrInput;
	JTextArea txtrSentences;
	JLabel lblInput;
	JLabel lblExtractedSentences;
	JScrollPane scrpInput;
	JScrollPane scrpSentences;
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InputFrame2 frame = new InputFrame2();
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
	public InputFrame2() {
		setTitle("GCD-NLP Step 1");
		initComponents();
		createEvents();
	}

	private void initComponents() {
		// TODO Auto-generated method stub
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][]", "[][grow][][][grow][]"));
		
		lblInput = new JLabel("Input:");
		lblExtractedSentences = new JLabel("Extracted Sentences:");
		
		txtrInput = new JTextArea();
		txtrInput.setLineWrap(true);
		txtrInput.setWrapStyleWord(true);
		
		scrpInput = new JScrollPane(txtrInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		txtrSentences = new JTextArea();
		txtrSentences.setLineWrap(true);
		txtrSentences.setWrapStyleWord(true);
		
		scrpSentences = new JScrollPane(txtrSentences, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		btnImportFile = new JButton("Import File");
		
		btnClearInputField = new JButton("Clear Input Field");
		
		btnGetSentences = new JButton("Get Sentences");
		btnNext = new JButton("Next");
		
		contentPane.add(lblInput, "cell 0 0");
		contentPane.add(scrpInput, "cell 0 1, span 3, grow");
		contentPane.add(btnImportFile, "cell 0 2");
		contentPane.add(btnClearInputField, "cell 0 2, align left");
		contentPane.add(btnGetSentences, "cell 2 2");
		contentPane.add(lblExtractedSentences, "cell 0 3");
		contentPane.add(scrpSentences, "cell 0 4, span 3, grow");
		contentPane.add(btnNext, "cell 2 5, align right");
	}

	private void createEvents() {
		// TODO Auto-generated method stub
		btnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(null);
				File inputFile = fileChooser.getSelectedFile();
				Scanner t = null;
				try {
					t = new Scanner(inputFile);
					String fileText = null;
					while(t.hasNext()) {
						fileText += t.next() + " ";
					}
					txtrInput.setText(fileText);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnClearInputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtrInput.setText("");
			}
		});
	}
}
