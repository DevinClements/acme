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
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ClientGUI implements Receiver {

	private final String PANEL_LOGIN = "login";
	private final String PANEL_MAIN = "main";
	private final String PANEL_ADD_EMPLOYEE = "employee_add";
	private final String PANEL_SUMMARY_EMPLOYEE = "employee_summary";
	
	private AcmeClient client;
	
	private JFrame frame;
	
	private JRadioButton rdbtnMainRegular;
	private JRadioButton rdbtnMainCallback;
	
	private JPanel containerMain;
	private JPanel panelDepartmentLogin;
	private JPanel containerDLogin;
	private JPanel panelMain;
	private JPanel panelPunch;
	private JPanel panelPunchContainer;
	private JPanel panelManager;
	private JPanel panelAddEmployee;
	private JPanel panelEmployeeSummary;
	private JPanel containerAddEmployeeForm;
	
	private JTextField txtEmployeeAddName;
	private JTextField txtEmployeeAddCode;
	private JTextField txtLoginDepartmentCode;
	
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
	
	private void goTo(String page) {
		CardLayout layout = (CardLayout) containerMain.getLayout();
		layout.show(containerMain, page);
	}
	
	public void didClickDepartmentLogin() {
		this.goTo(this.PANEL_MAIN);
		try {
			String departmentCode = txtLoginDepartmentCode.getText();
			client.login(departmentCode);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickPunch() {
		System.out.println("One Puuuuununnnnnch!!!!");
	}
	
	private void didClickAddEmployee() {
		this.goTo(PANEL_ADD_EMPLOYEE);
	}
	
	private void didClickEditEmployee() {
		System.out.println("Waiting to implement this.");
	}
	
	private void didClickEmployeeSummary() {
		this.goTo(PANEL_SUMMARY_EMPLOYEE);
	}
	
	private void didClickAddEmployeeSubmit() {
		this.goTo(PANEL_MAIN);
	}
	
	private void didClickAddEmployeeExit() {
		this.goTo(PANEL_MAIN);
	}
	
	private void didClickRemoveEmployee() {
		System.out.println("Removing Employee!");
	}
	
	private void didClickGoBack() {
		this.goTo(PANEL_MAIN);
	}
	
	private void didClickPunchRegular() {
		this.rdbtnMainCallback.setSelected(false);
	}
	
	private void didClickPunchCallback() {
		this.rdbtnMainRegular.setSelected(false);
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
		
		panelDepartmentLogin = new JPanel();
		containerMain.add(panelDepartmentLogin, PANEL_LOGIN);
		panelDepartmentLogin.setLayout(null);
		
		containerDLogin = new JPanel();
		containerDLogin.setBounds(77, 99, 296, 80);
		panelDepartmentLogin.add(containerDLogin);
		containerDLogin.setLayout(new BoxLayout(containerDLogin, BoxLayout.Y_AXIS));
		
		JLabel lblLoginDepartment = new JLabel("Department");
		lblLoginDepartment.setAlignmentX(Component.CENTER_ALIGNMENT);
		containerDLogin.add(lblLoginDepartment);
		lblLoginDepartment.setHorizontalAlignment(SwingConstants.CENTER);
		
		txtLoginDepartmentCode = new JTextField();
		containerDLogin.add(txtLoginDepartmentCode);
		txtLoginDepartmentCode.setHorizontalAlignment(SwingConstants.CENTER);
		txtLoginDepartmentCode.setColumns(10);
		
		JButton btnLoginSubmit = new JButton("Submit");
		btnLoginSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		containerDLogin.add(btnLoginSubmit);
		
		panelMain = new JPanel();
		containerMain.add(panelMain, PANEL_MAIN);
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelMain.add(tabbedPane);
		
		panelPunch = new JPanel();
		panelPunch.setBackground(UIManager.getColor("Button.background"));
		tabbedPane.addTab("Employee", null, panelPunch, null);
		panelPunch.setLayout(new BoxLayout(panelPunch, BoxLayout.X_AXIS));
		
		panelPunchContainer = new JPanel();
		panelPunch.add(panelPunchContainer);
		panelPunchContainer.setLayout(null);
		
		JLabel labelPunchTime = new JLabel("00:00 PM");
		labelPunchTime.setBounds(169, 68, 92, 26);
		labelPunchTime.setHorizontalAlignment(SwingConstants.CENTER);
		labelPunchTime.setFont(new Font("Helvetica Neue", Font.PLAIN, 21));
		panelPunchContainer.add(labelPunchTime);
		
		JButton buttonPunch = new JButton("Punch");
		buttonPunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickPunch();
			}
		});
		buttonPunch.setBounds(169, 106, 82, 29);
		panelPunchContainer.add(buttonPunch);
		
		rdbtnMainRegular = new JRadioButton("Regular");
		rdbtnMainRegular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickPunchRegular();
			}
		});
		rdbtnMainRegular.setSelected(true);
		rdbtnMainRegular.setBounds(126, 147, 82, 23);
		panelPunchContainer.add(rdbtnMainRegular);
		
		rdbtnMainCallback = new JRadioButton("Callback");
		rdbtnMainCallback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickPunchCallback();
			}
		});
		rdbtnMainCallback.setBounds(208, 147, 92, 23);
		panelPunchContainer.add(rdbtnMainCallback);
		
		panelManager = new JPanel();
		tabbedPane.addTab("Manager", null, panelManager, null);
		
		JList listEmployee = new JList();
		listEmployee.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEmployee.setModel(new AbstractListModel() {
			String[] values = new String[] {"Vincent More", "Arvell Webb"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		listEmployee.setSelectedIndex(0);
		listEmployee.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listEmployee.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
		
		JButton btnEmployeeAdd = new JButton("Add");
		btnEmployeeAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickAddEmployee();
			}
		});
		
		JButton btnEmployeeDelete = new JButton("Remove");
		btnEmployeeDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickRemoveEmployee();
			}
		});
		
		JButton btnEmployeeEdit = new JButton("Edit");
		btnEmployeeEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEditEmployee();
			}
		});
		
		JButton btnEmployeeSummary = new JButton("Summary");
		btnEmployeeSummary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEmployeeSummary();
			}
		});
		GroupLayout gl_panelManager = new GroupLayout(panelManager);
		gl_panelManager.setHorizontalGroup(
			gl_panelManager.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelManager.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnEmployeeAdd)
					.addGap(38)
					.addComponent(btnEmployeeDelete, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addGap(40)
					.addComponent(btnEmployeeEdit, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnEmployeeSummary)
					.addContainerGap())
				.addComponent(listEmployee, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
		);
		gl_panelManager.setVerticalGroup(
			gl_panelManager.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelManager.createSequentialGroup()
					.addContainerGap()
					.addComponent(listEmployee, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
					.addGap(18)
					.addGroup(gl_panelManager.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelManager.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnEmployeeAdd)
							.addComponent(btnEmployeeDelete))
						.addGroup(gl_panelManager.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnEmployeeSummary)
							.addComponent(btnEmployeeEdit)))
					.addContainerGap())
		);
		panelManager.setLayout(gl_panelManager);
		
		panelAddEmployee = new JPanel();
		containerMain.add(panelAddEmployee, PANEL_ADD_EMPLOYEE);
		
		containerAddEmployeeForm = new JPanel();
		
		JButton btnEmployeeAddSubmit = new JButton("Submit");
		btnEmployeeAddSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickAddEmployeeSubmit();
			}
		});
		
		JButton btnEmployeeAddExit = new JButton("Exit");
		btnEmployeeAddExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickAddEmployeeExit();
			}
		});
		GroupLayout gl_panelAddEmployee = new GroupLayout(panelAddEmployee);
		gl_panelAddEmployee.setHorizontalGroup(
			gl_panelAddEmployee.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAddEmployee.createSequentialGroup()
					.addGroup(gl_panelAddEmployee.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelAddEmployee.createSequentialGroup()
							.addGap(4)
							.addComponent(containerAddEmployeeForm, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
						.addGroup(gl_panelAddEmployee.createSequentialGroup()
							.addGap(181)
							.addComponent(btnEmployeeAddSubmit)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnEmployeeAddExit)))
					.addContainerGap())
		);
		gl_panelAddEmployee.setVerticalGroup(
			gl_panelAddEmployee.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAddEmployee.createSequentialGroup()
					.addGap(57)
					.addComponent(containerAddEmployeeForm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_panelAddEmployee.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnEmployeeAddSubmit)
						.addComponent(btnEmployeeAddExit))
					.addContainerGap(108, Short.MAX_VALUE))
		);
		
		JLabel lblAddEmployeeName = new JLabel("Name");
		
		txtEmployeeAddName = new JTextField();
		lblAddEmployeeName.setLabelFor(txtEmployeeAddName);
		txtEmployeeAddName.setColumns(10);
		
		JLabel labelAddEmployeeCode = new JLabel("Code");
		
		txtEmployeeAddCode = new JTextField();
		labelAddEmployeeCode.setLabelFor(txtEmployeeAddCode);
		txtEmployeeAddCode.setColumns(10);
		GroupLayout gl_containerAddEmployeeForm = new GroupLayout(containerAddEmployeeForm);
		gl_containerAddEmployeeForm.setHorizontalGroup(
			gl_containerAddEmployeeForm.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_containerAddEmployeeForm.createSequentialGroup()
					.addGap(44)
					.addGroup(gl_containerAddEmployeeForm.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_containerAddEmployeeForm.createSequentialGroup()
							.addComponent(lblAddEmployeeName)
							.addGap(102)
							.addComponent(txtEmployeeAddName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_containerAddEmployeeForm.createSequentialGroup()
							.addComponent(labelAddEmployeeCode)
							.addGap(106)
							.addComponent(txtEmployeeAddCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
		);
		gl_containerAddEmployeeForm.setVerticalGroup(
			gl_containerAddEmployeeForm.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_containerAddEmployeeForm.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_containerAddEmployeeForm.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_containerAddEmployeeForm.createSequentialGroup()
							.addGap(6)
							.addComponent(lblAddEmployeeName))
						.addComponent(txtEmployeeAddName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_containerAddEmployeeForm.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_containerAddEmployeeForm.createSequentialGroup()
							.addGap(6)
							.addComponent(labelAddEmployeeCode))
						.addComponent(txtEmployeeAddCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		);
		containerAddEmployeeForm.setLayout(gl_containerAddEmployeeForm);
		panelAddEmployee.setLayout(gl_panelAddEmployee);
		
		panelEmployeeSummary = new JPanel();
		containerMain.add(panelEmployeeSummary, PANEL_SUMMARY_EMPLOYEE);
		
		JLabel lblEmployeeSummaryName = new JLabel("Name:");
		
		JLabel lblEmployeeSummaryId = new JLabel("ID:");
		
		JLabel lblEmployeeSummaryStarted = new JLabel("Started:");
		
		JLabel lblEmployeeSummaryTotalHours = new JLabel("Total Hours:");
		
		JLabel lblEmployeeSummaryTotalPay = new JLabel("Total Pay:");
		
		JLabel lblEmployeeSummaryNameResult = new JLabel("");
		lblEmployeeSummaryNameResult.setLabelFor(lblEmployeeSummaryName);
		
		JLabel lblEmployeeSummaryIdResult = new JLabel("");
		lblEmployeeSummaryIdResult.setLabelFor(lblEmployeeSummaryId);
		
		JLabel lblEmployeeSummaryStartedResult = new JLabel("");
		lblEmployeeSummaryStartedResult.setLabelFor(lblEmployeeSummaryStarted);
		
		JLabel lblEmployeeSummaryTotalHoursResult = new JLabel("");
		lblEmployeeSummaryTotalHoursResult.setLabelFor(lblEmployeeSummaryTotalHours);
		
		JLabel lblEmployeeSummaryTotalPayResult = new JLabel("");
		lblEmployeeSummaryTotalPayResult.setLabelFor(lblEmployeeSummaryTotalPay);
		
		JButton btnGoBack = new JButton("Go Back");
		btnGoBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickGoBack();
			}
		});
		GroupLayout gl_panelEmployeeSummary = new GroupLayout(panelEmployeeSummary);
		gl_panelEmployeeSummary.setHorizontalGroup(
			gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
					.addGap(76)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnGoBack)
						.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
							.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblEmployeeSummaryTotalHours, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryTotalPay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryStarted, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryId, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
								.addComponent(lblEmployeeSummaryNameResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmployeeSummaryIdResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmployeeSummaryStartedResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmployeeSummaryTotalHoursResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmployeeSummaryTotalPayResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(192, Short.MAX_VALUE))
		);
		gl_panelEmployeeSummary.setVerticalGroup(
			gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
					.addGap(44)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmployeeSummaryName)
						.addComponent(lblEmployeeSummaryNameResult))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmployeeSummaryId)
						.addComponent(lblEmployeeSummaryIdResult))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmployeeSummaryStarted)
						.addComponent(lblEmployeeSummaryStartedResult))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmployeeSummaryTotalHours)
						.addComponent(lblEmployeeSummaryTotalHoursResult))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmployeeSummaryTotalPay)
						.addComponent(lblEmployeeSummaryTotalPayResult))
					.addPreferredGap(ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
					.addComponent(btnGoBack)
					.addGap(47))
		);
		panelEmployeeSummary.setLayout(gl_panelEmployeeSummary);
		btnLoginSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentLogin();
			}
		});
		frame.getContentPane().setLayout(groupLayout);
	}
}
