import javax.swing.*;

import net.proteanit.sql.DbUtils;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class PhonebookGUI {

	// creating connection to database using sqliteConnection class
	Connection conn = sqliteConnection.dbConnector(); 

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PhonebookGUI window = new PhonebookGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PhonebookGUI() {
		initialize();
	}

	// Method used to refresh JTable after a change like the ADD or CHANGE or REMOVE buttons
	public void refreshTable(){
		try(PreparedStatement pst = conn.prepareStatement("SELECT * FROM data ORDER BY First")){
			ResultSet rs = pst.executeQuery();
			table.setModel(DbUtils.resultSetToTableModel(rs));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		// this creates a new database if an existing one is not found like when first opening the jar file
		String start = "CREATE TABLE IF NOT EXISTS 'data' ('First' TEXT DEFAULT (null) ,'Last' TEXT DEFAULT (null) ,'Number' INTEGER DEFAULT (null) )";
		PreparedStatement create;
		try {
			create = conn.prepareStatement(start);
			create.execute();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		frame = new JFrame("Phonebook Application");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);
		
		// this is the search box on the top of the frame
		JTextField textField = new JTextField(10);
		panel_1.add(textField);
		
		// action for the search box
		// CURRENTLY ONLY WORKS WITH FIRST NAMES
		// Searched for name has to be an exact match!
		JButton btnNewButton = new JButton("Search");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {	
				try(PreparedStatement pst = conn.prepareStatement("SELECT * FROM data WHERE First LIKE '"+textField.getText()+"'")){
					ResultSet rs = pst.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(rs));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel_1.add(btnNewButton);

// RESET BUTTON	
		
		// meant to be used after a search to have the full list of names show up again
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent a) {	
			refreshTable();
		}
		});
		panel_1.add(btnReset);
		
//End of panel_1
//Need rs2xml.jar for this part, it's in the lib folder
		
		JPanel panel = new JPanel();	//JPanel for the JTable in the center
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		
		table = new JTable();			//JTable
		scrollPane.setViewportView(table);		
		
		try(PreparedStatement pst = conn.prepareStatement("SELECT * FROM data ORDER BY First")){	//populating JTable
			ResultSet rs = pst.executeQuery();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			table.setModel(DbUtils.resultSetToTableModel(rs));
			panel.add(scrollPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//End of panel for JTable
		
		JPanel panel_3 = new JPanel();	//JPanel for the bottom buttons
		frame.getContentPane().add(panel_3, BorderLayout.SOUTH);
		
// ADD BUTTON
		// KNOWN PROBLEM: currently phone numbers starting with a 1 (1XXXXXXXXX) do not work due to type issues
		JButton btnNewButton_2 = new JButton("Add");
		panel_3.add(btnNewButton_2);
		btnNewButton_2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				JPanel panel1 = new JPanel();
				JTextField textbox = new JTextField(10);
				JTextField textbox1 = new JTextField(10);
				JTextField textbox2 = new JTextField(10);
				JLabel label = new JLabel("First Name");
				JLabel label1 = new JLabel("Last Name");
				JLabel label2 = new JLabel("Phone Number");
				
				panel1.add(label);
				panel1.add(textbox);
				panel1.add(label1);
				panel1.add(textbox1);
				panel1.add(label2);
				panel1.add(textbox2);
				
				JDialog dialog = new JDialog();
				dialog.getContentPane().add(panel1);
				dialog.pack();
				dialog.setSize(new Dimension(150,220));
				dialog.setResizable(false);
				dialog.setVisible(true);
				
				JButton button = new JButton();
				button.setText("Enter");
				panel1.add(button, BorderLayout.NORTH);
				button.addActionListener(new ActionListener(){
					String sql = "INSERT INTO data(First,Last,Number) VALUES(?,?,?)";	//adding input info from add box to database
					public void actionPerformed(ActionEvent a){
						String fname = textbox.getText();
						String lname = textbox1.getText();
						Float number = Float.parseFloat(textbox2.getText());
						try(PreparedStatement pst = conn.prepareStatement(sql)){
							pst.setString(1,fname);
							pst.setString(2, lname);
							pst.setFloat(3, number);
							pst.executeUpdate();
							pst.close();
						} catch(SQLException e){
							e.printStackTrace();
						}
						dialog.setVisible(false);	
						refreshTable();											//end of input to database
					}
				});
			}
		});
		
// REMOVE BUTTON
		
		// this will remove any selected row and all other rows that have the same first and last name
		JButton btnNewButton_3 = new JButton("Remove");
		panel_3.add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				int selectedRowIndex = table.getSelectedRow();
				String selectedRow = (String) table.getModel().getValueAt(selectedRowIndex,0);
				Scanner console = new Scanner(selectedRow);
				String First = console.next();
				console.close();
				String selectedRow2 = (String) table.getModel().getValueAt(selectedRowIndex,1);
				Scanner console2 = new Scanner(selectedRow2);
				String Last = console2.next();
				console2.close();
				String sql = "DELETE FROM data WHERE First='"+First+"' AND Last='"+Last+"'";	
					try(PreparedStatement pst = conn.prepareStatement(sql)){
						pst.executeUpdate();
						pst.close();
					} catch(SQLException e){
						e.printStackTrace();
					}
					refreshTable();											
				}
			});

// CHANGE BUTTON
		
		// this will bring up a new window with the info of the selected row and then update any changes made
		// KNOWN PROBLEM: currently phone numbers starting with a 1 (1XXXXXXXXX) do not work due to type issues
		JButton btnNewButton_4 = new JButton("Change");
		btnNewButton_4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				
				//populating the input boxes with the selected row
				int selectedRowIndex = table.getSelectedRow();
				String selectedRow = (String) table.getModel().getValueAt(selectedRowIndex,0);
				Scanner console = new Scanner(selectedRow);
				String First = console.next();
				console.close();
				String selectedRow1 = (String) table.getModel().getValueAt(selectedRowIndex,1);
				Scanner console1 = new Scanner(selectedRow1);
				String Last = console1.next();
				console1.close();
				Long selectedRow2 = (Long) table.getModel().getValueAt(selectedRowIndex,2);
				String temp = ""+selectedRow2;
				Scanner console2 = new Scanner(temp);
				String Number = console2.next();
				console2.close();
				
				JPanel panel1 = new JPanel();
				JTextField textbox = new JTextField(10);
				JTextField textbox1 = new JTextField(10);
				JTextField textbox2 = new JTextField(10);
				JLabel label = new JLabel("First Name");
				JLabel label1 = new JLabel("Last Name");
				JLabel label2 = new JLabel("Phone Number");
				
				panel1.add(label);
				panel1.add(textbox);
				panel1.add(label1);
				panel1.add(textbox1);
				panel1.add(label2);
				panel1.add(textbox2);
				
				textbox.setText(First);
				textbox1.setText(Last);
				textbox2.setText(Number);
				
				JDialog dialog = new JDialog();
				dialog.getContentPane().add(panel1);
				dialog.pack();
				dialog.setSize(new Dimension(150,220));
				dialog.setResizable(false);
				dialog.setVisible(true);
				
				JButton button = new JButton();
				button.setText("Enter");
				panel1.add(button, BorderLayout.NORTH);
				button.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent a){

							String fname = textbox.getText();
							String lname = textbox1.getText();
							Float number = Float.parseFloat(textbox2.getText());
							String sql = "UPDATE data SET First='"+fname+"', Last='"+lname+"', Number='"+number+"' WHERE First='"+First+"'";	
							System.out.println(sql);
							try(PreparedStatement pst = conn.prepareStatement(sql)){
								pst.executeUpdate();
								pst.close();
							} catch(SQLException e){
								e.printStackTrace();
							}
						
						dialog.setVisible(false);
						refreshTable();
					}
				});
			}
		});
		panel_3.add(btnNewButton_4);
	
	}
}