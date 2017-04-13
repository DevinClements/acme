**CS 4321 - Software Engineering 1**

**Problem Statement – Version 1**

January 31, 2017

The Acme Corporation has a local headquarters in Valdosta, GA. The local headquarters, Acme-Valdosta, needs a system that allows hourly employees to clock in and out and allows an accounting manager to manage this data and generate accounting reports.

Acme-Valdosta is composed of departments which have a unique 3 digit code (numeric) and each department has a number of employees each with a unique 4 digit ID (numeric). Departments are classified as “Production” or “Indirect Production” (“Prod” and “Indirect” for short) depending on whether the job tasks of employees in that department directly support production or indirectly support production.

When employees arrive at work they clock-in at a kiosk. Typically, and employee will clock-in when they arrive at lunch, clock-out for lunch, clock back in after lunch, and then clock-out when they leave for the day. However, there can be many variations of this pattern. Sometimes they forget to clock out for lunch, thus if an employee has been clocked in for at least 4 hours and has not taken lunch, then 30 minutes is deducted from the total for the day. Also, an employee can clock in and out any number of times in a day, for instance if they have a doctor’s appointment, _etc. _

There are two types of clock in/outs. A regular clock-in takes place when an employee shows up for a scheduled work assignment. Another type of clock-in is called a “call-back”. This occurs when an employee is called to come in to work outside of their scheduled work hours, for example a machine is broken, _etc_. Employees on a call-backs are always paid for a minimum of 4 hours no matter if they work less than this. However, the actual amount of work done on the call-back is recorded (via a clock-out). For example, if an employee is called back and ends up taking 1 hour to complete the task, she still gets paid for 4 hours.

Employees get paid an hourly rate which can increase to time-and-a-half under certain situations. Regular hours are the basis for determining when time-and-a-half kicks in. When regular hours exceed 40, the subsequent regular hours and all call-back hours that were actually worked receive time-and-a-half. Call-back hours do not contribute to regular hours. Thus, if an employee worked 8 hours Monday-Friday, and was called back Tuesday and did one hour work, then he would be paid for 44 hours at the hourly rate. If that same employee had worked 10 hours on Wednesday, instead of 8, then he would be paid 43 hours (40 regular hours, and 3 non-work call-back hours) at the hourly rate and 3 hours (2 from the scheduled work week and 1 from the call-back) at time-and-a-half. Call-back hours that are not worked are never paid at time-and-a-half.

In addition to regular and call-back, time can be attributed to: Holiday, Vacation, Jury Duty, and Bereavement and these hours are entered by a manager. Holiday hours do contribute to regular hours, but none of the other types do nor can they ever be paid at time-and-a-half. Examples:

- Suppose Monday is a holiday. An employee gets 8 hours of pay that contributes to regular hours. If they work 10 hours Tuesday, and then 8 hours Wednesday-Friday, they will get paid 40 hours at the hourly rate and 2 hours at time-and-a-half. 

- Suppose an Employee takes Monday as a vacation (or jury duty or bereavement) and then works 10 hours Tuesday-Friday then the employee will get paid for 48 hours at the hourly rate. If that employee is called back on Saturday and works 2 hours, then they are paid 52 hours at the hourly rate. 

- Suppose an Employee takes Monday as a vacation (or jury duty or bereavement) and then works 12 hours Tuesday and 10 Wednesday-Friday then the employee will get paid for 48 hours at the hourly rate and 2 hours at time-and-a-half. If that employee is called back on Saturday and works 2 hours, then they are paid 50 hours at the hourly rate and 4 hours at time-and-a-half.

An employee who retires may have accrued vacation time which is entered by a manager as a single entry. For example, if the employee retired on Jan 3, and had 32 hours of vacation time when then retired, this would be enetered, as: clock-in: 8:00am, Jan 4; clock-out: 4:00pm Jan 5.

A manager can also edit any clock-in and clock-out data that has been entered by an employee including the type of clock-in. For example, if an employee forgets to clock-out, or they accidently clock-in as a call-back when in fact it is a regular clock-in.

Currently, Acme-Valdosta has plants in 3 cities, Valdosta, Douglass, and Adel with a clock-in kiosk at each. An employee that is going to clock-in simply types her ID and then chooses “R” or “C”, for Regular or Call-Back, and then presses “Clock-In”. The system then records the date and time of the clock-in. For a clock-out, the employee types her ID and presses “Clock-out”. Valdosta is the headquarters, where the management console is located. Clock-ins are sent directly to Valdosta upon being entered provided the network is up. If the network is down, the system should cache the clock-ins and upload them in a batch once the network is back up.

The following Pay Types should be used for various reports that require them and should be listed in this order:

| Pay Type | Description                |
|----------|----------------------------|
| REG      | Regular Production         |
| OT       | Overtime Direct Production |
| HOL      | Holiday Production         |
| VAC      | Vacation Production        |
| BRV      | Bereavement                |
| JUR      | Jury Duty Production       |

All reports cover one week and begin on Monday and end on Sunday. An employee who clocks-in at 10:30 pm or after on a Sunday is credited with work for the next week. Though not likely, care must be taken so that an employee who clocks-in at say 10 pm on Sunday and out 3 hours later at 1 am Monday is credited with the previous week.

The following reports are required (samples are provided in another attachment):

| Title        | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Dept – Full  | Departments are list in numerical order. Within departments, employees are listed alphabetically showing hours and pay for any of the pay types that apply. The totals for each department are shown listing the hours and pay for each pay type that applies following the list of employees for the department. Finally, totals are shown over all departments. Hours should be listed with one decimal and pay should be in currency format.                                                                                            |
| Dept – Brief | Departments are list in numerical order. The totals for each department are shown listing the hours and pay for each pay type. Finally, totals are shown over all departments.                                                                                                                                                                                                                                                                                                                                                             |
| Emp          | Employees are listed alphabetically along with their department ID and production type and hours and pay for each pay type. Totals are shown over all employees.                                                                                                                                                                                                                                                                                                                                                                           |
| Export       | This is a comma delimited file listing employees alphabetically in this exact format: **Name,Emp ID,Dept ID,Reg Hours,Reg Pay, OT Hours,OT Pay,Tot Hours,TotPay** There should be no formatting of any kind, i.e. not $ signs, but do provide two decimals for pay and one decimal for hours. This report goes to corporate headquarters and has a slightly different meaning for Regular hours. Regular hours for the export report include all pay types except over time. Over time has the same definition as is used in the local plants. |
| Time Card    | See sample                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
