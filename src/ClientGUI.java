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
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class ClientGUI implements Receiver {

	private JFrame frame;
	private JTextField txtDepartmentCode;
	private AcmeClient client;
	private JPanel containerMain;
	
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
			case "!department-login":
				System.out.printf("Success: %s\n", (String) message.objects[0]);
				
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
			String departmentCode = txtDepartmentCode.getText();
			client.login(departmentCode);
			CardLayout layout = (CardLayout) containerMain.getLayout();
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
		
		containerMain = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(containerMain, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(containerMain, GroupLayout.PREFERRED_SIZE, 278, GroupLayout.PREFERRED_SIZE)
		);
		containerMain.setLayout(new CardLayout(0, 0));
		
		JPanel panelDepartmentLogin = new JPanel();
		containerMain.add(panelDepartmentLogin, "name_1992096590687377");
		panelDepartmentLogin.setLayout(null);
		
		JPanel containerDLogin = new JPanel();
		containerDLogin.setBounds(77, 99, 296, 80);
		panelDepartmentLogin.add(containerDLogin);
		containerDLogin.setLayout(new BoxLayout(containerDLogin, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel("Department");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		containerDLogin.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		txtDepartmentCode = new JTextField();
		containerDLogin.add(txtDepartmentCode);
		txtDepartmentCode.setHorizontalAlignment(SwingConstants.CENTER);
		txtDepartmentCode.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		containerDLogin.add(btnSubmit);
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentLogin();
			}
		});
		frame.getContentPane().setLayout(groupLayout);
	}
}
