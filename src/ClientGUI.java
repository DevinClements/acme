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
import javax.swing.DefaultListModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ClientGUI implements Receiver {

	private final String PANEL_LOGIN = "login";
	private final String PANEL_MAIN = "main";
	private final String PANEL_ADD_EMPLOYEE = "employee_add";
	private final String PANEL_SUMMARY_EMPLOYEE = "employee_summary";
	private final String PANEL_CREATE = "create";
	private final String PANEL_EDIT_EMPLOYEE = "employee_edit";
	
	private AcmeClient client;
	
	private JFrame frame;
	
	private JRadioButton rdbtnMainRegular;
	private JRadioButton rdbtnMainCallback;
	private JRadioButton rdbtnCreateDepartmentProduction;
	private JRadioButton rdbtnCreateDepartmentIndirectProduction;
	
	private JPanel containerMain;
	private JPanel panelDepartmentLogin;
	private JPanel panelMain;
	private JPanel panelPunch;
	private JPanel panelManager;
	private JPanel panelAddEmployee;
	private JPanel panelEmployeeSummary;
	private JPanel containerAddEmployeeForm;
	
	private JTextField txtEmployeeAddName;
	private JTextField txtEmployeeAddCode;
	private JTextField txtLoginDepartmentCode;
	private JTextField txtCreateDepartmentCode;
	private JTextField txtMainEmployeeCode;
	private JTextField txtEmployeeSummaryFrom;
	private JTextField txtEmployeeSummaryTo;
	private JTextField txtEmployeeEditDate;
	private JTextField txtEmployeeEditClockIn;
	private JTextField txtEmployeeEditClockOut;
	
	private JLabel lblEmployeeSummaryNameResult;
	private JLabel lblEmployeeSummaryIdResult;
	private JLabel lblEmployeeSummaryStartedResult;
	private JLabel lblEmployeeSummaryTotalHoursResult;
	private JLabel lblEmployeeSummaryTotalPayResult;
	
	private JList<String> listEmployee;
	private JList<String> listEmployeeEditTicket;
	
	private JComboBox comboEmployeeEditType;
	
	// Data Model
	String deptCode = "";
	DefaultListModel<String> employeeModel = new DefaultListModel<String>();
	DefaultListModel<String> employeeEditDates = new DefaultListModel<String>();
	ArrayList<Employee> employees = new ArrayList<Employee>();
	Timesheet employeeEditTimesheet;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Calendar swag = Calendar.getInstance();
		swag.set(Calendar.DAY_OF_WEEK, 1);
		System.out.println(swag.getTime());
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
				this.txtMainEmployeeCode.setText("");
				break;
			case "!timesheet":
				Timesheet editSheet = (Timesheet) message.objects[0];
				this.employeeEditTimesheet = editSheet;
				this.employeeEditDates.clear();
				for(Ticket ticket : this.employeeEditTimesheet.tickets) {
					if(ticket.ticketType == TicketType.ClockIn) {
						this.employeeEditDates.addElement(ticket.datetime.toString());
					}
				}
				break;
			case "!timesheet-range":
				Timesheet sheet = (Timesheet) message.objects[0];
				Employee emp = this.employees.get(this.listEmployee.getSelectedIndex());
				this.lblEmployeeSummaryNameResult.setText(emp.name);
				this.lblEmployeeSummaryIdResult.setText(emp.id);
				this.lblEmployeeSummaryTotalHoursResult.setText("" + sheet.getHoursWorked());
				this.lblEmployeeSummaryTotalPayResult.setText("" + sheet.getHoursPaid());
				break;
			case "!ticket-replace":
				this.clearEditPanel();
				this.didClickEmployeeEditGetDate();
				break;
			case "!department-login":
				System.out.printf("Success: %s\n", (String) message.objects[0]);
				this.goTo(this.PANEL_MAIN);
				try {
					this.client.getEmployees();
				} catch(IOException e) {
					System.out.println(e);
				}
				break;
			case "!department-create":
				System.out.println("Created department.");
				this.goTo(this.PANEL_LOGIN);
				break;
			case "!employee-list":
				this.refreshEmployeeList((Employee[]) message.objects);
				break;
			case "!employee-add":
				try {
					this.client.getEmployees();
					this.txtEmployeeAddCode.setText("");
					this.txtEmployeeAddName.setText("");
				} catch(IOException e) {
					System.out.println(e);
				}
				this.goTo(PANEL_MAIN);
				break;
			case "!employee-remove":
				try {
					this.client.getEmployees();
				} catch(IOException e) {
					System.out.println(e);
				}
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
	
	private void didSelectEmployeeEditDate() {
		this.txtEmployeeEditClockIn.setText("");
		this.txtEmployeeEditClockOut.setText("");
		int index = this.listEmployeeEditTicket.getSelectedIndex();
		Ticket ticketClockIn = this.employeeEditTimesheet.tickets.get(index);
		Ticket ticketClockOut = this.employeeEditTimesheet.tickets.get(index+1);
		this.txtEmployeeEditClockIn.setText(ticketClockIn.datetime.toString());
		this.txtEmployeeEditClockOut.setText(ticketClockOut.datetime.toString());
		this.comboEmployeeEditType.setSelectedItem(ticketClockIn.hourType);
	}
	
	private void goTo(String page) {
		CardLayout layout = (CardLayout) containerMain.getLayout();
		layout.show(containerMain, page);
	}
	
	private void refreshEmployeeList(Employee[] employees) {
		this.employeeModel.removeAllElements();
		this.employees.clear();
		for(Employee employee: employees) {
			this.employeeModel.addElement(employee.name);
			this.employees.add(employee);
		}
	}
	
	public void didClickDepartmentLogin() {
		try {
			String departmentCode = txtLoginDepartmentCode.getText();
			client.login(departmentCode);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickDepartmentCreateSubmit() {
		try {
			String departmentCode = this.txtCreateDepartmentCode.getText();
			DepartmentType type = null;
			if(this.rdbtnCreateDepartmentProduction.isSelected()) {
				type = DepartmentType.Production;
			}
			if(this.rdbtnCreateDepartmentIndirectProduction.isSelected()) {
				type = DepartmentType.IndirectProduction;
			}
			client.createDepartment(departmentCode, type);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickPunch() {
		if(this.txtMainEmployeeCode.getText().isEmpty()) {
			return;
		}
		String id = this.txtMainEmployeeCode.getText();
		HourType type = null;
		if(this.rdbtnMainRegular.isSelected()) {
			type = HourType.Regular;
		}
		if(this.rdbtnMainCallback.isSelected()) {
			type = HourType.Callback;
		}
		try {
			this.client.punch(id, type);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickAddEmployee() {
		this.goTo(PANEL_ADD_EMPLOYEE);
	}
	
	private void didClickEditEmployee() {
		this.goTo(this.PANEL_EDIT_EMPLOYEE);
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		
		String dateString = "" + month + "-" + day + "-" + year;
		this.txtEmployeeEditDate.setText(dateString);
		
		Employee selectedEmployee = this.employees.get(this.listEmployee.getSelectedIndex());
		
		try {
			this.client.getTimesheet(selectedEmployee.id, new Date[]{date});
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickEmployeeSummary() {
		this.goTo(PANEL_SUMMARY_EMPLOYEE);
		Calendar currentDate = Calendar.getInstance();
		int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
		int currentMonth = currentDate.get(Calendar.MONTH) + 1;
		int currentYear = currentDate.get(Calendar.YEAR);
		String dateString = "" + currentMonth + "-" + currentDay + "-" + currentYear;
		this.txtEmployeeSummaryFrom.setText(dateString);
		this.txtEmployeeSummaryTo.setText(dateString);
	}
	
	private void didClickAddEmployeeSubmit() {
		String id = this.txtEmployeeAddCode.getText();
		String name = this.txtEmployeeAddName.getText();
		try {
			this.client.addEmployee(id, name);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickAddEmployeeExit() {
		this.goTo(PANEL_MAIN);
	}
	
	private void didClickEmployeeSummaryCalculate() {
		String fromText = this.txtEmployeeSummaryFrom.getText();
		String toText = this.txtEmployeeSummaryTo.getText();
		String[] fromTextDelimited = fromText.split("-");
		String[] toTextDelimited = toText.split("-");
		
		int fromMonth = Integer.parseInt(fromTextDelimited[0]) - 1;
		int fromDay = Integer.parseInt(fromTextDelimited[1]);
		int fromYear = Integer.parseInt(fromTextDelimited[2]);
		
		int toMonth = Integer.parseInt(toTextDelimited[0]) - 1;
		int toDay = Integer.parseInt(toTextDelimited[1]);
		int toYear = Integer.parseInt(toTextDelimited[2]);
		
		Calendar calendarFrom = Calendar.getInstance();
		Calendar calendarTo = Calendar.getInstance();
		
		calendarFrom.set(fromYear, fromMonth, fromDay);
		calendarTo.set(toYear, toMonth, toDay);
		
		int index = this.listEmployee.getSelectedIndex();
		Employee emp = this.employees.get(index);
		
		try {
			this.client.getTimesheet(emp.id, calendarFrom.getTime(), calendarTo.getTime());
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickRemoveEmployee() {
		int index = listEmployee.getSelectedIndex();
		Employee employeeToRemove = employees.get(index);
		try {
			this.client.removeEmployee(employeeToRemove.id);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickGoBack() {
		this.lblEmployeeSummaryIdResult.setText("");
		this.lblEmployeeSummaryNameResult.setText("");
		this.lblEmployeeSummaryStartedResult.setText("");
		this.lblEmployeeSummaryTotalHoursResult.setText("");
		this.lblEmployeeSummaryTotalPayResult.setText("");
		this.txtEmployeeSummaryFrom.setText("");
		this.txtEmployeeSummaryTo.setText("");
		this.goTo(PANEL_MAIN);
	}
	
	private void didClickDepartmentCreateCancel() {
		this.goTo(PANEL_LOGIN);
	}
	
	private void didClickDepartmentCreateProduction() {
		this.rdbtnCreateDepartmentIndirectProduction.setSelected(false);
	}
	
	private void didClickDepartmentCreateIndirect() {
		this.rdbtnCreateDepartmentProduction.setSelected(false);
	}
	
	private void didClickPunchRegular() {
		this.rdbtnMainCallback.setSelected(false);
	}
	
	private void didClickPunchCallback() {
		this.rdbtnMainRegular.setSelected(false);
	}

	private void didClickLoginCreate() {
		this.goTo(PANEL_CREATE);
	}
	
	private void clearEditPanel() {
		this.listEmployeeEditTicket.clearSelection();
		this.txtEmployeeEditClockIn.setText("");
		this.txtEmployeeEditClockOut.setText("");
		this.comboEmployeeEditType.setSelectedItem(HourType.Regular);
	}
	
	private void didClickEmployeeEditClear() {
		this.clearEditPanel();
	}
	
	private void didClickEmployeeEditGetDate() {
		this.clearEditPanel();
		
		String dateText = this.txtEmployeeEditDate.getText();
		String[] dateTextDelimited = dateText.split("-");
		
		int toMonth = Integer.parseInt(dateTextDelimited[0]) - 1;
		int toDay = Integer.parseInt(dateTextDelimited[1]);
		int toYear = Integer.parseInt(dateTextDelimited[2]);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(toYear, toMonth, toDay);
		
		int index = this.listEmployee.getSelectedIndex();
		Employee emp = this.employees.get(index);
		
		try {
			this.client.getTimesheet(emp.id, new Date[]{calendar.getTime()});
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void didClickEmployeeEditGoBack() {
		this.goTo(this.PANEL_MAIN);
	}
	
	private void didClickEmployeeEditApply() {
		Employee employee = this.employees.get(this.listEmployee.getSelectedIndex());
		String textClockIn = this.txtEmployeeEditClockIn.getText();
		String textClockOut = this.txtEmployeeEditClockOut.getText();
		Ticket[] toRemove = new Ticket[2];
		Ticket[] toAdd = new Ticket[2];
		
		if(this.listEmployeeEditTicket.isSelectionEmpty()) {
			if(textClockIn.isEmpty() || textClockOut.isEmpty()) {
				return;
			} else {
				Date dateClockIn = getDateFromString(textClockIn);
				Date dateClockOut = getDateFromString(textClockOut);
				HourType type = (HourType) this.comboEmployeeEditType.getSelectedItem();
				Ticket ticketClockIn = new Ticket(dateClockIn, type, TicketType.ClockIn, employee.id);
				Ticket ticketClockOut = new Ticket(dateClockOut, type, TicketType.ClockIn, employee.id);
				toAdd[0] = ticketClockIn;
				toAdd[1] = ticketClockOut;
			}
		} else {
			if(textClockIn.isEmpty() || textClockOut.isEmpty()) {
				Date dateClockIn = getDateFromString(textClockIn);
				Date dateClockOut = getDateFromString(textClockOut);
				System.out.println(dateClockIn);
				HourType type = (HourType) this.comboEmployeeEditType.getSelectedItem();
				Ticket ticketClockIn = new Ticket(dateClockIn, type, TicketType.ClockIn, employee.id);
				Ticket ticketClockOut = new Ticket(dateClockOut, type, TicketType.ClockIn, employee.id);
				toRemove[0] = ticketClockIn;
				toRemove[1] = ticketClockOut;
			} else {
				int selectedIndex = this.listEmployeeEditTicket.getSelectedIndex();
				Ticket ticketClockInRemove = this.employeeEditTimesheet.tickets.get(selectedIndex);
				Ticket ticketClockOutRemove = this.employeeEditTimesheet.tickets.get(selectedIndex+1);
				toRemove[0] = ticketClockInRemove;
				toRemove[1] = ticketClockOutRemove;
				
				Date dateClockIn = getDateFromString(textClockIn);
				Date dateClockOut = getDateFromString(textClockOut);
				HourType type = (HourType) this.comboEmployeeEditType.getSelectedItem();
				Ticket ticketClockInAdd = new Ticket(dateClockIn, type, TicketType.ClockIn, employee.id);
				Ticket ticketClockOutAdd = new Ticket(dateClockOut, type, TicketType.ClockIn, employee.id);
				toAdd[0] = ticketClockInAdd;
				toAdd[1] = ticketClockOutAdd;
			}
		}
		toAdd = toAdd.length == 0 ? null : toAdd;
		toRemove = toRemove.length == 0 ? null : toRemove;
		
		try {
			this.client.replaceTickets(employee.id, toRemove, toAdd);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private Date getDateFromString(String dateString) {
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		Date date = new Date();
		try {
			date = new SimpleDateFormat(pattern).parse(dateString);
		} catch(ParseException e) {
			System.out.println(e);
		}
		return date;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					client.disconnect();
				} catch(IOException ex) {
					System.out.println(ex);
				}
			}
		});
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
		
		JLabel lblLoginDepartment = new JLabel("Department");
		lblLoginDepartment.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblLoginDepartment.setHorizontalAlignment(SwingConstants.CENTER);
		
		txtLoginDepartmentCode = new JTextField();
		txtLoginDepartmentCode.setHorizontalAlignment(SwingConstants.CENTER);
		txtLoginDepartmentCode.setColumns(10);
		
		JButton btnLoginSubmit = new JButton("Submit");
		btnLoginSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton btnLoginCreate = new JButton("Create");
		btnLoginCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickLoginCreate();
			}
		});
		btnLoginCreate.setAlignmentX(0.5f);
		GroupLayout gl_panelDepartmentLogin = new GroupLayout(panelDepartmentLogin);
		gl_panelDepartmentLogin.setHorizontalGroup(
			gl_panelDepartmentLogin.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDepartmentLogin.createSequentialGroup()
					.addContainerGap(100, Short.MAX_VALUE)
					.addGroup(gl_panelDepartmentLogin.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panelDepartmentLogin.createSequentialGroup()
							.addComponent(lblLoginDepartment)
							.addGap(184))
						.addGroup(Alignment.TRAILING, gl_panelDepartmentLogin.createSequentialGroup()
							.addGroup(gl_panelDepartmentLogin.createParallelGroup(Alignment.LEADING)
								.addComponent(txtLoginDepartmentCode, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panelDepartmentLogin.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnLoginSubmit)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnLoginCreate, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))
							.addGap(130))))
		);
		gl_panelDepartmentLogin.setVerticalGroup(
			gl_panelDepartmentLogin.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDepartmentLogin.createSequentialGroup()
					.addGap(76)
					.addComponent(lblLoginDepartment)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtLoginDepartmentCode, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelDepartmentLogin.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoginSubmit)
						.addComponent(btnLoginCreate))
					.addContainerGap(110, Short.MAX_VALUE))
		);
		panelDepartmentLogin.setLayout(gl_panelDepartmentLogin);
		btnLoginSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentLogin();
			}
		});
		
		panelMain = new JPanel();
		containerMain.add(panelMain, PANEL_MAIN);
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelMain.add(tabbedPane);
		
		panelPunch = new JPanel();
		panelPunch.setBackground(UIManager.getColor("Button.background"));
		tabbedPane.addTab("Employee", null, panelPunch, null);
		
		JButton buttonPunch = new JButton("Punch");
		
		rdbtnMainRegular = new JRadioButton("Regular");
		rdbtnMainRegular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickPunchRegular();
			}
		});
		rdbtnMainRegular.setSelected(true);
		
		JLabel labelPunchTime = new JLabel("00:00 PM");
		labelPunchTime.setHorizontalAlignment(SwingConstants.CENTER);
		labelPunchTime.setFont(new Font("Helvetica Neue", Font.PLAIN, 21));
		
		rdbtnMainCallback = new JRadioButton("Callback");
		
		txtMainEmployeeCode = new JTextField();
		txtMainEmployeeCode.setColumns(10);
		
		JLabel lblCode = new JLabel("Emp. Code:");
		lblCode.setLabelFor(txtMainEmployeeCode);
		GroupLayout gl_panelPunch = new GroupLayout(panelPunch);
		gl_panelPunch.setHorizontalGroup(
			gl_panelPunch.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panelPunch.createSequentialGroup()
					.addGap(35)
					.addGroup(gl_panelPunch.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(buttonPunch, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panelPunch.createSequentialGroup()
							.addGroup(gl_panelPunch.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblCode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(rdbtnMainRegular, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelPunch.createParallelGroup(Alignment.LEADING)
								.addComponent(txtMainEmployeeCode, 0, 0, Short.MAX_VALUE)
								.addComponent(rdbtnMainCallback))))
					.addPreferredGap(ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
					.addComponent(labelPunchTime, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
					.addGap(53))
		);
		gl_panelPunch.setVerticalGroup(
			gl_panelPunch.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPunch.createSequentialGroup()
					.addGroup(gl_panelPunch.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelPunch.createSequentialGroup()
							.addGap(50)
							.addGroup(gl_panelPunch.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtMainEmployeeCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCode, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(gl_panelPunch.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnMainRegular)
								.addComponent(rdbtnMainCallback))
							.addGap(18)
							.addComponent(buttonPunch, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelPunch.createSequentialGroup()
							.addGap(84)
							.addComponent(labelPunchTime, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(56, Short.MAX_VALUE))
		);
		panelPunch.setLayout(gl_panelPunch);
		rdbtnMainCallback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickPunchCallback();
			}
		});
		buttonPunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickPunch();
			}
		});
		
		panelManager = new JPanel();
		tabbedPane.addTab("Manager", null, panelManager, null);
		
		listEmployee = new JList<String>(this.employeeModel);
		listEmployee.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
			gl_panelManager.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelManager.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnEmployeeAdd)
					.addGap(38)
					.addComponent(btnEmployeeDelete, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addGap(40)
					.addComponent(btnEmployeeEdit, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnEmployeeSummary)
					.addContainerGap())
				.addComponent(listEmployee, GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
		);
		gl_panelManager.setVerticalGroup(
			gl_panelManager.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelManager.createSequentialGroup()
					.addContainerGap()
					.addComponent(listEmployee, GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
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
		
		lblEmployeeSummaryNameResult = new JLabel("......");
		lblEmployeeSummaryNameResult.setLabelFor(lblEmployeeSummaryName);
		
		lblEmployeeSummaryIdResult = new JLabel("......");
		lblEmployeeSummaryIdResult.setLabelFor(lblEmployeeSummaryId);
		
		lblEmployeeSummaryStartedResult = new JLabel("......");
		lblEmployeeSummaryStartedResult.setLabelFor(lblEmployeeSummaryStarted);
		
		lblEmployeeSummaryTotalHoursResult = new JLabel("......");
		lblEmployeeSummaryTotalHoursResult.setLabelFor(lblEmployeeSummaryTotalHours);
		
		lblEmployeeSummaryTotalPayResult = new JLabel("......");
		lblEmployeeSummaryTotalPayResult.setLabelFor(lblEmployeeSummaryTotalPay);
		
		JButton btnGoBack = new JButton("Go Back");
		btnGoBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickGoBack();
			}
		});
		
		JLabel lblEmployeeSummaryFrom = new JLabel("From:");
		
		JLabel lblEmployeeSummaryTo = new JLabel("To:");
		
		txtEmployeeSummaryFrom = new JTextField();
		txtEmployeeSummaryFrom.setColumns(10);
		
		txtEmployeeSummaryTo = new JTextField();
		txtEmployeeSummaryTo.setColumns(10);
		
		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEmployeeSummaryCalculate();
			}
		});
		GroupLayout gl_panelEmployeeSummary = new GroupLayout(panelEmployeeSummary);
		gl_panelEmployeeSummary.setHorizontalGroup(
			gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
							.addGap(26)
							.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblEmployeeSummaryTotalHours, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryTotalPay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryStarted, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryId, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEmployeeSummaryName))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
									.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
										.addComponent(lblEmployeeSummaryNameResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblEmployeeSummaryIdResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblEmployeeSummaryStartedResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
									.addGap(18)
									.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
											.addComponent(lblEmployeeSummaryFrom)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(txtEmployeeSummaryFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
											.addComponent(lblEmployeeSummaryTo, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(txtEmployeeSummaryTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
								.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
									.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
										.addComponent(lblEmployeeSummaryTotalHoursResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblEmployeeSummaryTotalPayResult, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnCalculate))))
						.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
							.addGap(178)
							.addComponent(btnGoBack)))
					.addContainerGap(48, Short.MAX_VALUE))
		);
		gl_panelEmployeeSummary.setVerticalGroup(
			gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
					.addGap(31)
					.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
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
								.addComponent(lblEmployeeSummaryTotalPayResult)))
						.addGroup(gl_panelEmployeeSummary.createSequentialGroup()
							.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblEmployeeSummaryFrom)
								.addComponent(txtEmployeeSummaryFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEmployeeSummary.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblEmployeeSummaryTo)
								.addComponent(txtEmployeeSummaryTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addComponent(btnCalculate)))
					.addGap(68)
					.addComponent(btnGoBack)
					.addGap(41))
		);
		panelEmployeeSummary.setLayout(gl_panelEmployeeSummary);
		
		JPanel panelCreateDepartment = new JPanel();
		containerMain.add(panelCreateDepartment, this.PANEL_CREATE);
		
		txtCreateDepartmentCode = new JTextField();
		txtCreateDepartmentCode.setColumns(10);
		
		JLabel lblCreateDepartmentCode = new JLabel("Code:");
		lblCreateDepartmentCode.setLabelFor(txtCreateDepartmentCode);
		
		JLabel lblCreateDepartmentType = new JLabel("Type:");
		
		rdbtnCreateDepartmentProduction = new JRadioButton("Production");
		rdbtnCreateDepartmentProduction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentCreateProduction();
			}
		});
		rdbtnCreateDepartmentProduction.setSelected(true);
		
		rdbtnCreateDepartmentIndirectProduction = new JRadioButton("Indirect Production");
		rdbtnCreateDepartmentIndirectProduction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentCreateIndirect();
			}
		});
		
		JButton btnDepartmentCreateSubmit = new JButton("Submit");
		btnDepartmentCreateSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentCreateSubmit();
			}
		});
		
		JButton btnDepartmentCreateCancel = new JButton("Cancel");
		btnDepartmentCreateCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickDepartmentCreateCancel();
			}
		});
		GroupLayout gl_panelCreateDepartment = new GroupLayout(panelCreateDepartment);
		gl_panelCreateDepartment.setHorizontalGroup(
			gl_panelCreateDepartment.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelCreateDepartment.createSequentialGroup()
					.addGap(49)
					.addGroup(gl_panelCreateDepartment.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelCreateDepartment.createSequentialGroup()
							.addComponent(lblCreateDepartmentCode)
							.addGap(18)
							.addComponent(txtCreateDepartmentCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelCreateDepartment.createSequentialGroup()
							.addComponent(lblCreateDepartmentType, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_panelCreateDepartment.createParallelGroup(Alignment.LEADING, false)
								.addComponent(rdbtnCreateDepartmentProduction, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(rdbtnCreateDepartmentIndirectProduction, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
								.addGroup(gl_panelCreateDepartment.createSequentialGroup()
									.addComponent(btnDepartmentCreateSubmit)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnDepartmentCreateCancel, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap(165, Short.MAX_VALUE))
		);
		gl_panelCreateDepartment.setVerticalGroup(
			gl_panelCreateDepartment.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelCreateDepartment.createSequentialGroup()
					.addGap(43)
					.addGroup(gl_panelCreateDepartment.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtCreateDepartmentCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCreateDepartmentCode))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelCreateDepartment.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCreateDepartmentType)
						.addComponent(rdbtnCreateDepartmentProduction))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnCreateDepartmentIndirectProduction)
					.addGap(18)
					.addGroup(gl_panelCreateDepartment.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDepartmentCreateSubmit)
						.addComponent(btnDepartmentCreateCancel))
					.addContainerGap(96, Short.MAX_VALUE))
		);
		panelCreateDepartment.setLayout(gl_panelCreateDepartment);
		
		JPanel panelEmployeeEdit = new JPanel();
		containerMain.add(panelEmployeeEdit, this.PANEL_EDIT_EMPLOYEE);
		
		txtEmployeeEditDate = new JTextField();
		txtEmployeeEditDate.setColumns(10);
		
		listEmployeeEditTicket = new JList<String>(this.employeeEditDates);
		listEmployeeEditTicket.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!listEmployeeEditTicket.isSelectionEmpty()) {
					didSelectEmployeeEditDate();
				}
			}
		});
		listEmployeeEditTicket.setBorder(UIManager.getBorder("TextField.border"));
		
		JLabel lblEmployeeEditClockIn = new JLabel("Clock In:");
		
		JLabel lblEmployeeEditClockOut = new JLabel("Clock Out:");
		
		txtEmployeeEditClockIn = new JTextField();
		txtEmployeeEditClockIn.setColumns(10);
		
		txtEmployeeEditClockOut = new JTextField();
		txtEmployeeEditClockOut.setColumns(10);
		
		comboEmployeeEditType = new JComboBox();
		comboEmployeeEditType.setModel(new DefaultComboBoxModel(HourType.values()));
		
		JLabel lblEmployeeEditHourType = new JLabel("Type:");
		
		JButton btnEmployeeEditApply = new JButton("Apply");
		btnEmployeeEditApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEmployeeEditApply();
			}
		});
		
		JButton btnEmployeeEditClear = new JButton("Clear");
		btnEmployeeEditClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEmployeeEditClear();
			}
		});
		
		JButton btnEmployeeEditApplyGoBack = new JButton("Go Back");
		btnEmployeeEditApplyGoBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEmployeeEditGoBack();
			}
		});
		
		JButton btnEmployeeEditGetDate = new JButton("Get Date");
		btnEmployeeEditGetDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didClickEmployeeEditGetDate();
			}
		});
		GroupLayout gl_panelEmployeeEdit = new GroupLayout(panelEmployeeEdit);
		gl_panelEmployeeEdit.setHorizontalGroup(
			gl_panelEmployeeEdit.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEmployeeEdit.createSequentialGroup()
					.addGap(12)
					.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.LEADING, false)
						.addComponent(listEmployeeEditTicket, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnEmployeeEditGetDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(txtEmployeeEditDate))
					.addPreferredGap(ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
					.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnEmployeeEditApply, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_panelEmployeeEdit.createSequentialGroup()
							.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.LEADING)
									.addComponent(lblEmployeeEditClockIn)
									.addComponent(lblEmployeeEditClockOut))
								.addComponent(lblEmployeeEditHourType, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtEmployeeEditClockOut)
								.addComponent(txtEmployeeEditClockIn)
								.addComponent(comboEmployeeEditType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panelEmployeeEdit.createSequentialGroup()
							.addComponent(btnEmployeeEditClear, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnEmployeeEditApplyGoBack)
							.addGap(12)))
					.addGap(41))
		);
		gl_panelEmployeeEdit.setVerticalGroup(
			gl_panelEmployeeEdit.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEmployeeEdit.createSequentialGroup()
					.addContainerGap()
					.addComponent(txtEmployeeEditDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panelEmployeeEdit.createSequentialGroup()
							.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblEmployeeEditClockIn)
								.addComponent(txtEmployeeEditClockIn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtEmployeeEditClockOut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmployeeEditClockOut))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboEmployeeEditType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmployeeEditHourType))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnEmployeeEditApply, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
							.addGroup(gl_panelEmployeeEdit.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnEmployeeEditClear)
								.addComponent(btnEmployeeEditApplyGoBack))
							.addGap(13))
						.addGroup(gl_panelEmployeeEdit.createSequentialGroup()
							.addComponent(btnEmployeeEditGetDate)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(listEmployeeEditTicket, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)))
					.addGap(17))
		);
		panelEmployeeEdit.setLayout(gl_panelEmployeeEdit);
		frame.getContentPane().setLayout(groupLayout);
	}
}
