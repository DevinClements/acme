import java.io.IOException;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Box;
import java.awt.Component;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI implements Receiver {

	private JFrame frame;
	private JTextField txtEnterDepartmentCode;
	private AcmeClient client;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
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
	public ClientGUI() {
		try {
			client = new AcmeClient("localhost", 5555, this);
			client.connect();
		} catch(IOException e) {
			System.out.println(e);
		}
		initialize();
	}
	
	public void receive(Message message) {
		switch(message.command) {
			case "!punch":
				System.out.printf("We punched the timeclock.\n");
				break;
			case "!timesheet":
				Timesheet sheet = (Timesheet) message.objects[0];
				System.out.println(sheet.getHoursWorked());
				break;
			case "!success":
				System.out.printf("Success: %s\n", (String) message.objects[0]);
				break;
			case "!error":
				System.out.printf("Error: %s\n", (String) message.objects[0]);
				break;
			default:
				System.out.printf("Invalid command.\n");
				break;
		}
	}
	
	public void didClickDepartmentLogin() {
		try {
			String departmentCode = txtEnterDepartmentCode.getText();
			client.login(departmentCode);
		} catch(IOException e) {
			System.out.println(e);
		}
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.PREFERRED_SIZE, 278, GroupLayout.PREFERRED_SIZE)
		);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		JLabel lblNewLabel = new JLabel("Department");
		sl_panel.putConstraint(SpringLayout.WEST, lblNewLabel, 188, SpringLayout.WEST, panel);
		panel.add(lblNewLabel);
		
		txtEnterDepartmentCode = new JTextField();
		sl_panel.putConstraint(SpringLayout.SOUTH, lblNewLabel, -26, SpringLayout.NORTH, txtEnterDepartmentCode);
		sl_panel.putConstraint(SpringLayout.NORTH, txtEnterDepartmentCode, 127, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, txtEnterDepartmentCode, 157, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, txtEnterDepartmentCode, -158, SpringLayout.EAST, panel);
		txtEnterDepartmentCode.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txtEnterDepartmentCode);
		txtEnterDepartmentCode.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentLogin();
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, btnSubmit, 23, SpringLayout.SOUTH, txtEnterDepartmentCode);
		sl_panel.putConstraint(SpringLayout.WEST, btnSubmit, 181, SpringLayout.WEST, panel);
		panel.add(btnSubmit);
		frame.getContentPane().setLayout(groupLayout);
	}
}
