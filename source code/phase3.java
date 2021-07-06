import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.text.*; 

public class phase3 implements ActionListener {
	public class request{
		public String date;
		public int requestNo;
		public String status;
		public String completedDate;
		public String[] services;
		public char approvalRequired;
		public int requestedBy;
		public int processedBy;
		public int department;
	}
	Vector <request> allRequests = new Vector <request> ();
	public class person{
		public int id;
		public String name;
		public String[] mobileNo;
		public int department;
	}
	person allPersons[];
	public class staff extends person{
		public int id;
	}
	staff allStaff[];
	public class ictsStaff extends person{
		public int id;
		public void processRequest(int requestNumber) {
			try {
				ps = con.prepareStatement("update request set status = ? where request_no = ?");
				ps.setInt(2, requestNumber);
				ps.setString(1, "C");
				ps.executeUpdate();
				ps = con.prepareStatement("update request set completed_date = ? where request_no = ?");
				long millis=System.currentTimeMillis();  
		        java.sql.Date date=new java.sql.Date(millis);  
		    	ps.setDate(1,date);
		    	ps.setInt(2, requestNumber);
		    	ps.executeUpdate();
		    	int index = 0;
		    	for(int i=0;i<allRequests.size();i++) {
		    		if(allRequests.elementAt(i).requestNo == requestNumber) index = i;
		    	}
		    	allRequests.elementAt(index).status = "C";
			    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			    Date today = Calendar.getInstance().getTime();   
			    allRequests.elementAt(index).completedDate = df.format(today);
		    	
			}
			catch(Exception E) {
				System.out.println(E.getMessage());
			}
		}
	}
	ictsStaff allIctsStaff[];
	public class department{
		public int deptNo;
		public String deptName;
		public int hod;
		public void allotJob(request req) {
			try {
				ps = con.prepareStatement("select processed_by, count(*) from request where department = ? group by processed_by having processed_by is not null");
				ps.setInt(1,req.department);
				rs = ps.executeQuery();
				rs.next();
				int minRequestProcessor = rs.getInt("processed_by");
				int minRequests = rs.getInt("count");
				while(rs.next()) {
					if(minRequests>rs.getInt("count")) {
						minRequests = rs.getInt("count");
						minRequestProcessor = rs.getInt("processed_by");
					}
				}
				req.status = "P";
				req.processedBy = minRequestProcessor;
				ps = con.prepareStatement("update request set status = ? where request_no = ?");
				ps.setInt(2,req.requestNo);
				ps.setString(1,req.status);
				ps.executeUpdate();
				ps = con.prepareStatement("update request set processed_by = ? where request_no = ?");
				ps.setInt(1,req.processedBy);
				ps.setInt(2, req.requestNo);
				ps.executeUpdate();
				for(int i=0;i<allRequests.size();i++) {
					if(allRequests.elementAt(i).requestNo == req.requestNo) { 
					allRequests.elementAt(i).status = "P";
					allRequests.elementAt(i).processedBy = minRequestProcessor;
					}
				}
			}
			catch(Exception E) {
				System.out.println(E.getMessage());
			}
		}
		public void deny(int id) {
			try {
				ps = con.prepareStatement("update request set status = ? where request_no = ?");
				ps.setInt(2, id);
				ps.setString(1, "AD");
				ps.executeUpdate();
				for(int i=0;i<allRequests.size();i++) {
					if(allRequests.elementAt(i).requestNo == id) {
						allRequests.elementAt(i).status = "AD";
					}
				}
			}
			catch(Exception E) {
				System.out.println(E.getMessage());
			}
			
		}
	}
	department allDepartments[];
	private Connection con = null;
	private Statement st = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	{
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project", "postgres", "yaswanth@1050");
			st = con.createStatement();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	JFrame f ;
	JLabel welcomeLabel,requestPageLabels[],requestPageMessage,requestDetailsPageRequestNumberLabel,requestDetailsPageMessage,passwordMessage;
	JButton homeButtons[],requestPageOkButton,backToHomeButton,requestDetailsPageButton,ILoginButton,HLoginButton;
	JTextField requestPageInputs[],requestDetailsPageInput,ILoginPageTF1,ILoginPageTF2,HLoginPageTF1,HLoginPageTF2;
	JComboBox requestPageDepartmentComboBox ;
	JLabel requestDetailsPageLabel1[],requestDetailsPageLabel2[],ILoginPageLabel1,ILoginPageLabel2,HLoginPageLabel1,HLoginPageLabel2;
	JLabel ILoginMessage,HLoginMessage,ALoginPageLabel,ALoginPageMessage,ALabel,processRequestsLabel,processRequestsMessage,approveRequestsLabel,approveRequestsMessage;
	JTextField ALoginPageInput,processRequestsInput,approveRequestsInput;
	JButton ALoginPageButton,processRequestsButton,approveRequestsButton,approveDenyButton;
	JTextArea persons,staff,department,IStaff,requests,IPageRequests,HPageRequests;
	JScrollPane AScroll[],IScrollPane,HScrollPane ;
	int currentILogin;
	int currentHLogin;
	public Boolean isEqual(String a,String b) {
		Boolean fl = true;
		for(int i=0;i<Math.min(a.length(),b.length());i++) {
			if(a.charAt(i) != b.charAt(i)) fl = false;
		}
		return fl;
	}
	public int getIntFromResultSet(ResultSet Rs) {
		int res = 0;
		try {
			Rs.next();
			res = Rs.getInt("count");
		}
		catch(Exception E){
			System.out.println(E.getMessage());
		}
		return res;
	}
	public void clearAll() {
		welcomeLabel.setBounds(0,0,0,0);
		for(int i=0;i<6;i++) {
			homeButtons[i].setBounds(0,0,0,0);
		}
		for(int i=0;i<3;i++) {
			requestPageLabels[i].setBounds(0,0,0,0);
			if(i!=2) {
				requestPageInputs[i].setText("");
				requestPageInputs[i].setBounds(0,0,0,0);
			}
		}
		requestPageOkButton.setBounds(0,0,0,0);
		backToHomeButton.setBounds(0,0,0,0);
		requestPageMessage.setBounds(0,0,0,0);
		requestPageMessage.setText("");
		requestPageDepartmentComboBox.setBounds(0,0,0,0);
		requestDetailsPageRequestNumberLabel.setBounds(0,0,0,0);
		requestDetailsPageInput.setText("");
		requestDetailsPageInput.setBounds(0,0,0,0);
		requestDetailsPageMessage.setBounds(0,0,0,0);
		requestDetailsPageMessage.setText("");
		requestDetailsPageButton.setBounds(0,0,0,0);
		for(int i=0;i<11;i++) {
			requestDetailsPageLabel1[i].setBounds(0,0,0,0);
			requestDetailsPageLabel2[i].setBounds(0,0,0,0);
		}
		ILoginPageLabel1.setBounds(0,0,0,0);
		ILoginPageLabel2.setBounds(0,0,0,0);
		HLoginPageLabel1.setBounds(0,0,0,0);
		HLoginPageLabel2.setBounds(0,0,0,0);
		ILoginPageTF1.setBounds(0,0,0,0);
		ILoginPageTF2.setBounds(0,0,0,0);
		HLoginPageTF1.setBounds(0,0,0,0);
		HLoginPageTF2.setBounds(0,0,0,0);
		ILoginPageTF1.setText("");
		ILoginPageTF2.setText("");
		HLoginPageTF1.setText("");
		HLoginPageTF2.setText("");
		ILoginButton.setBounds(0,0,0,0);
		HLoginButton.setBounds(0,0,0,0);
		ILoginMessage.setBounds(0,0,0,0);
		HLoginMessage.setBounds(0,0,0,0);
		ILoginMessage.setText("");
		HLoginMessage.setText("");
		ALoginPageButton.setBounds(0,0,0,0);
		ALoginPageLabel.setBounds(0,0,0,0);
		ALoginPageMessage.setBounds(0,0,0,0);
		ALoginPageInput.setBounds(0,0,0,0);
		ALoginPageInput.setText("");
		ALoginPageMessage.setText("");
		ALabel.setBounds(0,0,0,0);
		for(int i=0;i<5;i++) AScroll[i].setBounds(0,0,0,0);
		IScrollPane.setBounds(0,0,0,0);
		HScrollPane.setBounds(0,0,0,0);
		IPageRequests.setBounds(0,0,0,0);
		HPageRequests.setBounds(0,0,0,0);
		processRequestsLabel.setBounds(0,0,0,0);
		processRequestsInput.setBounds(0,0,0,0);
		processRequestsInput.setText("");
		processRequestsMessage.setBounds(0,0,0,0);
		processRequestsMessage.setText("");
		processRequestsButton.setBounds(0,0,0,0);
		IPageRequests.setText("");
		approveRequestsLabel.setBounds(0,0,0,0);
		approveRequestsInput.setBounds(0,0,0,0);
		approveRequestsInput.setText("");
		approveRequestsMessage.setBounds(0,0,0,0);
		approveRequestsMessage.setText("");
		approveRequestsButton.setBounds(0,0,0,0);
		approveDenyButton.setBounds(0,0,0,0);
		HPageRequests.setText("");
		passwordMessage.setBounds(0,0,0,0);
	}
	public String get20(String s) {
		String res = "";
		for(int i=0;i<s.length();i++) {
			res+=s.charAt(i);
		}
		while(res.length()<=20) res+=" ";
		return res;
	}
	public String get10(String s) {
		String res = "";
		for(int i=0;i<s.length();i++) {
			res+=s.charAt(i);
		}
		while(res.length()<=10) {
			res+=" ";
		}
		return res;
	}
	public void loadHomePage() {
		clearAll();
		welcomeLabel.setBounds(400,20,700,50);
		for(int i=0;i<6;i++) {
			homeButtons[i].setBounds(520,200+70*i,350,50);
		}
		passwordMessage.setBounds(400,650,650,50);
	}
	public void loadAdminLoginPage() {
		clearAll();
		ALoginPageLabel.setBounds(500,150,300,50);
		ALoginPageInput.setBounds(650,150,300,50);
		ALoginPageMessage.setBounds(600,230,300,50);
		ALoginPageButton.setBounds(500,300,100,50);
		backToHomeButton.setBounds(650,300,300,50);
		ALoginPageMessage.setText("password : java");
	}
	public void loadRequestPlacingPage() {
		clearAll();
		for(int i=0;i<3;i++) {
			requestPageLabels[i].setBounds(250,100+70*i,350,70);
			if(i!=2) requestPageInputs[i].setBounds(600,100+70*i+13,400,40);
		}
		requestPageOkButton.setBounds(500,350,100,50);
		backToHomeButton.setBounds(650,350,300,50);
		requestPageMessage.setBounds(500,420,600,50);
		requestPageDepartmentComboBox.setBounds(600,100+70*2+13,400,40);
	}
	public void loadRequestDetailsPage() {
		clearAll();
		requestDetailsPageRequestNumberLabel.setBounds(350,50,500,50);
		requestDetailsPageInput.setBounds(650,50,300,50);
		requestDetailsPageMessage.setBounds(500,130,500,50);
		requestDetailsPageButton.setBounds(450,200,200,50);
		backToHomeButton.setBounds(700,200,300,50);
	}
	public void loadICTSLoginPage() {
		clearAll();
		ILoginPageLabel1.setBounds(500,100,200,40);
		ILoginPageLabel2.setBounds(500,150,200,40);
		ILoginPageTF1.setBounds(600,100,250,40);
		ILoginPageTF2.setBounds(600,150,250,40);
		ILoginButton.setBounds(500,250,150,50);
		backToHomeButton.setBounds(680,250,300,50);
		ILoginMessage.setBounds(500,320,500,50);
	}
	public void loadAdminPage() {
		clearAll();
		try {
			String k = "";
			k+="All persons\n";
			k+=get20("id");
			k+=get20("name");
			k+='\n';
			k+='\n';
			rs = st.executeQuery("select * from person");
			while(rs.next()) {
				k+=get20(Integer.toString(rs.getInt("id")));
				k+=get20(rs.getString("name"));
				k+='\n';
			}
			AScroll[0].setBounds(20,70,250,600);
			persons.setText(k);
			k="All departments\n";
			k+=get10("departno");
			k+=get10("dept name");
			k+=get10("HOD");
			k+="\n\n";
			for(int i=0;i<allDepartments.length;i++) {
				k+=get10(Integer.toString(allDepartments[i].deptNo));
				k+=get10(allDepartments[i].deptName);
				k+=get10(Integer.toString(allDepartments[i].hod));
				k+="\n";
			}
			department.setText(k);
			AScroll[1].setBounds(300,70,250,130);
			rs = st.executeQuery("select * from staff");
			k = "All Staff";
			k+="\n";
			k+=get20("id");
			k+="\n\n";
			while(rs.next()) {
				k+=get20(Integer.toString(rs.getInt("id")));
				k+="\n";
			}
			staff.setText(k);
			AScroll[2].setBounds(600,70,100,160);
			rs = st.executeQuery("select * from ICTS_staff");
			k = "All ICTS Staff";
			k+="\n";
			k+=get20("id");
			k+="\n\n";
			while(rs.next()) {
				k+=get20(Integer.toString(rs.getInt("id")));
				k+="\n";
			}
			IStaff.setText(k);
			AScroll[3].setBounds(750,70,100,160);
			k = "All requests\n";
			k+=get20("request no");
			k+=get20("requeted by");
			k+=get20("date");
			k+=get20("status");
			k+=get20("completed date");
			k+=get20("approval required");
			k+=get20("processed by");
			k+=get20("department");
			k+="\n\n";
			for(int i=0;i<allRequests.size();i++) {
				k += get20(Integer.toString(allRequests.elementAt(i).requestNo));
				k+="        ";
				k += get20(Integer.toString(allRequests.elementAt(i).requestedBy));
				k+="        ";
				k += get20(allRequests.elementAt(i).date);
				k+="  ";
				String status = allRequests.elementAt(i).status;
				k += get20(status);
				k+="        ";
				if(isEqual("C",status)) k += get20(allRequests.elementAt(i).completedDate);
				else k+="null                        ";
				k += get20(Character.toString(allRequests.elementAt(i).approvalRequired));
				k+="        ";
				if(isEqual("P",status) || isEqual("C",status)) k += get20(Integer.toString(allRequests.elementAt(i).processedBy));
				else k+="null                ";
				k+="        ";
				k += get20(Integer.toString(allRequests.elementAt(i).department));
				k+="\n";
			}
			requests.setText(k);
			backToHomeButton.setBounds(100,750,300,50);
			AScroll[4].setBounds(300,300,1000,320);
		}
		catch(Exception E) {
			System.out.println(E.getMessage());
		}
		ALabel.setBounds(600,30,200,40);
	}
	public void loadProcessingPage(int id) {
		clearAll();
		IScrollPane.setBounds(200,250,1000,800);
		processRequestsLabel.setBounds(100,0,700,100);
		processRequestsInput.setBounds(750,30,500,50);
		processRequestsMessage.setBounds(500,100,500,50);
		processRequestsButton.setBounds(400,150,100,50);
		backToHomeButton.setBounds(600,150,300,50);
		currentILogin = id;
		try {
			ps = con.prepareStatement("select request_no from request where processed_by = ? and status = ?");
			ps.setInt(1,id);
			ps.setString(2,"P");
			rs = ps.executeQuery();
			String k = "All Requests that are being processed by you \n\n";
			while(rs.next()){
				int Id = rs.getInt("request_no");
				k+="Request number : ";
				k+=Integer.toString(Id);
				k+='\n';
				ps = con.prepareStatement("select requested_by from request where request_no = ?");
				ResultSet temp ;
				ps.setInt(1, Id);
				temp = ps.executeQuery();
				temp.next();
				int requestedBy = temp.getInt("requested_by");
				ps = con.prepareStatement("select name from person where id = ?");
				ps.setInt(1, requestedBy);
				temp= ps.executeQuery();
				temp.next();
				String requestedByName = temp.getString("name");
				ps = con.prepareStatement("select mobile_no from person_mobile where id = ?");
				ps.setInt(1, requestedBy);
				temp = ps.executeQuery();
				Vector <String> requestedByMobile = new Vector <String> ();
				while(temp.next()) {
					requestedByMobile.add(temp.getString("mobile_no"));
				}
				int index = 0;
				for(int i=0;i<allRequests.size();i++) {
					if(allRequests.elementAt(i).requestNo == Id) index = i;
				}
				k+="Requestsed by (id) : ";
				k+=Integer.toString(requestedBy);
				k+='\n';
				k+="Requestsed by (name) : ";
				k+=requestedByName;
				k+='\n';
				k+="Mobile number(s) : ";
				for(int i=0;i<requestedByMobile.size();i++) {
					k+=requestedByMobile.elementAt(i);
					if(i != requestedByMobile.size()-1) k += ", ";
				}
				k+='\n';
				k+="Date : ";
				k+=allRequests.elementAt(index).date;
				k+='\n';
				k+="Service(s) : ";
				for(int i=0;i<allRequests.elementAt(index).services.length;i++) {
					k += allRequests.elementAt(index).services[i];
					if(i!=allRequests.elementAt(index).services.length-1) k += ", ";
				}
				k+="\n\n";
			}
			IPageRequests.setText(k);
		}
		catch(Exception E) {
			System.out.println(E.getMessage());
		}
	}
	public void loadApprovalPage(int id) {
		clearAll();
		HScrollPane.setBounds(200,250,1000,800);
		approveRequestsLabel.setBounds(100,0,700,100);
		approveRequestsInput.setBounds(750,30,500,50);
		approveRequestsMessage.setBounds(500,100,500,50);
		approveRequestsButton.setBounds(400,150,150,50);
		backToHomeButton.setBounds(800,150,300,50);
		approveDenyButton.setBounds(600,150,150,50);
		currentHLogin = id;
		try {
			ps = con.prepareStatement("select request_no from request where department = ? and status = ?");
			ps.setInt(1,id);
			ps.setString(2,"WA");
			rs = ps.executeQuery();
			String k = "All Requests that are waiting for your approval \n\n";
			while(rs.next()){
				int Id = rs.getInt("request_no");
				k+="Request number : ";
				k+=Integer.toString(Id);
				k+='\n';
				ps = con.prepareStatement("select requested_by from request where request_no = ?");
				ResultSet temp ;
				ps.setInt(1, Id);
				temp = ps.executeQuery();
				temp.next();
				int requestedBy = temp.getInt("requested_by");
				ps = con.prepareStatement("select name from person where id = ?");
				ps.setInt(1, requestedBy);
				temp= ps.executeQuery();
				temp.next();
				String requestedByName = temp.getString("name");
				ps = con.prepareStatement("select mobile_no from person_mobile where id = ?");
				ps.setInt(1, requestedBy);
				temp = ps.executeQuery();
				Vector <String> requestedByMobile = new Vector <String> ();
				while(temp.next()) {
					requestedByMobile.add(temp.getString("mobile_no"));
				}
				int index = 0;
				for(int i=0;i<allRequests.size();i++) {
					if(allRequests.elementAt(i).requestNo == Id) index = i;
				}
				k+="Requestsed by (id) : ";
				k+=Integer.toString(requestedBy);
				k+='\n';
				k+="Requestsed by (name) : ";
				k+=requestedByName;
				k+='\n';
				k+="Mobile number(s) : ";
				for(int i=0;i<requestedByMobile.size();i++) {
					k+=requestedByMobile.elementAt(i);
					if(i != requestedByMobile.size()-1) k += ", ";
				}
				k+='\n';
				k+="Date : ";
				k+=allRequests.elementAt(index).date;
				k+='\n';
				k+="Service(s) : ";
				for(int i=0;i<allRequests.elementAt(index).services.length;i++) {
					k += allRequests.elementAt(index).services[i];
					if(i!=allRequests.elementAt(index).services.length-1) k += ", ";
				}
				k+="\n\n";
			}
			HPageRequests.setText(k);
		}
		catch(Exception E) {
			System.out.println(E.getMessage());
		}
	}
	public void loadHODLoginPage() {
		clearAll();
		HLoginPageLabel1.setBounds(500,100,200,40);
		HLoginPageLabel2.setBounds(500,150,200,40);
		HLoginPageTF1.setBounds(600,100,250,40);
		HLoginPageTF2.setBounds(600,150,250,40);
		HLoginButton.setBounds(500,250,150,50);
		backToHomeButton.setBounds(680,250,300,50);
		HLoginMessage.setBounds(500,320,500,50);
	}
	phase3(){
		try {
			allPersons = new person[getIntFromResultSet(st.executeQuery("select count(*) from person"))];
			rs = st.executeQuery("select * from person");
			int count = 0;
			while(rs.next()) {
				person Person = new person();
				Person.id = rs.getInt("id");
				Person.name = rs.getString("name");
				Person.department = rs.getInt("department");
				ps = con.prepareStatement("select count(*) from person_mobile where id = ?");
				ps.setInt(1, Person.id);
				int numberOfMobileNumbers = getIntFromResultSet(ps.executeQuery());
				Person.mobileNo = new String[numberOfMobileNumbers];
				ps = con.prepareStatement("select mobile_no from person_mobile where id = ?");
				ps.setInt(1, Person.id);
				ResultSet temp = ps.executeQuery();
				for(int i=0;i<numberOfMobileNumbers;i++) {
					temp.next();
					Person.mobileNo[i] = temp.getString("mobile_no");
				}
				allPersons[count++] = Person; 
			}
			allStaff = new staff[getIntFromResultSet(st.executeQuery("select count(*) from staff"))];
			rs = st.executeQuery("select * from staff");
			count = 0;
			while(rs.next()) {
				staff Staff = new staff();
				Staff.id = rs.getInt("id");
				allStaff[count++] = Staff;
			}
			allIctsStaff = new ictsStaff[getIntFromResultSet(st.executeQuery("select count(*) from ICTS_staff"))];
			rs = st.executeQuery("select * from ICTS_staff");
			count = 0;
			while(rs.next()) {
				ictsStaff is = new ictsStaff();
				is.id = rs.getInt("id");
				allIctsStaff[count++] = is;
			}
			allDepartments = new department[getIntFromResultSet(st.executeQuery("select count(*) from department"))];
			rs = st.executeQuery("select * from department");
			count = 0;
			while(rs.next()) {
				department Department = new department();
				Department.deptNo = rs.getInt("Dept_no");
				Department.deptName = rs.getString("dept_name");
				Department.hod = rs.getInt("HOD");
				allDepartments[count++] = Department;
			}
			rs = st.executeQuery("select * from request");
			while(rs.next()) {
				request Request = new request();
				Request.requestNo = rs.getInt("request_no");
				Request.approvalRequired = rs.getString("approval_required").charAt(0);
				Request.date = rs.getString("date");
				Request.completedDate = rs.getString("completed_date");
				Request.department = rs.getInt("department");
				Request.requestedBy = rs.getInt("requested_by");
				Request.processedBy = rs.getInt("processed_by");
				Request.status = rs.getString("status");
				int numberOfServices = 0;
				ps = con.prepareStatement("select count(*) from services where request_no = ?");
				ps.setInt(1,Request.requestNo);
				numberOfServices = getIntFromResultSet(ps.executeQuery());
				Request.services = new String[numberOfServices];
				ps = con.prepareStatement("select service from services where request_no = ?");
				ps.setInt(1, Request.requestNo);
				ResultSet temp = ps.executeQuery();
				for(int i=0;i<numberOfServices;i++) {
					temp.next();
					Request.services[i] = temp.getString("service");
				}
				allRequests.add(Request);
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		f = new JFrame("Project");
		welcomeLabel = new JLabel("Welcome to ICTS management system");
		welcomeLabel.setFont(new Font("",Font.BOLD,30));
		homeButtons = new JButton[6];
		for(int i=0;i<6;i++) {
			homeButtons[i] = new JButton();
			homeButtons[i].setFont(new Font("",Font.BOLD,22));
			f.add(homeButtons[i]);
			homeButtons[i].addActionListener(this);
		}
		requestPageLabels = new JLabel[3];
		requestPageInputs = new JTextField[2];
		for(int i=0;i<3;i++) {
			if(i!=2) {
				requestPageInputs[i] = new JTextField();
				requestPageInputs[i].setFont(new Font("",Font.PLAIN,18));
			}
			requestPageLabels[i] = new JLabel();
			requestPageLabels[i].setFont(new Font("",Font.PLAIN,18));
			f.add(requestPageLabels[i]);
			if(i!=2) f.add(requestPageInputs[i]);
		}
		requestDetailsPageLabel1 = new JLabel[11];
		requestDetailsPageLabel2 = new JLabel[11];
		for(int i=0;i<11;i++) {
			requestDetailsPageLabel1[i] = new JLabel();
			requestDetailsPageLabel2[i] = new JLabel();
			requestDetailsPageLabel1[i].setFont(new Font("",Font.PLAIN,16));
			requestDetailsPageLabel2[i].setFont(new Font("",Font.PLAIN,16));
			f.add(requestDetailsPageLabel1[i]);
			f.add(requestDetailsPageLabel2[i]);
		}
		passwordMessage = new JLabel("You can leave PIN/password fields as it is not a real application");
		passwordMessage.setFont(new Font("",Font.BOLD,18));
		f.add(passwordMessage);
		approveDenyButton = new JButton("Deny");
		approveDenyButton.setFont(new Font("",Font.BOLD,18));
		approveDenyButton.addActionListener(this);
		f.add(approveDenyButton);
		approveRequestsButton = new JButton("Approve");
		approveRequestsButton.setFont(new Font("",Font.BOLD,18));
		approveRequestsButton.addActionListener(this);
		f.add(approveRequestsButton);
		approveRequestsMessage = new JLabel();
		approveRequestsMessage.setFont(new Font("",Font.ITALIC,16));
		f.add(approveRequestsMessage);
		approveRequestsInput = new JTextField();
		approveRequestsInput.setFont(new Font("",Font.PLAIN,18));
		f.add(approveRequestsInput);
		approveRequestsLabel = new JLabel("Enter the processing completed requests request number(s) : (seperated by ',')");
		approveRequestsLabel.setFont(new Font("",Font.PLAIN,16));
		f.add(approveRequestsLabel);
		processRequestsButton = new JButton("OK");
		processRequestsButton.setFont(new Font("",Font.BOLD,18));
		processRequestsButton.addActionListener(this);
		f.add(processRequestsButton);
		processRequestsMessage = new JLabel();
		processRequestsMessage.setFont(new Font("",Font.ITALIC,16));
		f.add(processRequestsMessage);
		processRequestsInput = new JTextField();
		processRequestsInput.setFont(new Font("",Font.PLAIN,18));
		f.add(processRequestsInput);
		processRequestsLabel = new JLabel("Enter the processing completed requests request number(s) : (seperated by ',')");
		processRequestsLabel.setFont(new Font("",Font.PLAIN,16));
		f.add(processRequestsLabel);
		IPageRequests = new JTextArea();
		HPageRequests = new JTextArea();
		IPageRequests.setFont(new Font("",Font.PLAIN,18));
		HPageRequests.setFont(new Font("",Font.PLAIN,18));
		IScrollPane = new JScrollPane(IPageRequests);
		HScrollPane = new JScrollPane(HPageRequests);
		f.add(IScrollPane);
		f.add(HScrollPane);
		IPageRequests.setEditable(false);
		HPageRequests.setEditable(false);
		staff = new JTextArea();
		IStaff = new JTextArea();
		department = new JTextArea();
		requests = new JTextArea();
		persons = new JTextArea();
		persons.setFont(new Font("",Font.PLAIN,14));
		staff.setFont(new Font("",Font.PLAIN,14));
		department.setFont(new Font("",Font.PLAIN,14));
		IStaff.setFont(new Font("",Font.PLAIN,14));
		requests.setFont(new Font("",Font.PLAIN,14));
		staff.setEditable(false);
		department.setEditable(false);
		IStaff.setEditable(false);
		requests.setEditable(false);
		persons.setEditable(false);
		AScroll = new JScrollPane[5];
		AScroll[0] = new JScrollPane(persons);
		AScroll[1] = new JScrollPane(department);
		AScroll[2] = new JScrollPane(staff);
		AScroll[3] = new JScrollPane(IStaff);
		AScroll[4] = new JScrollPane(requests);
		for(int i=0;i<5;i++) f.add(AScroll[i]);
		ALabel = new JLabel("The entire data");
		ALabel.setFont(new Font("",Font.BOLD,20));
		f.add(ALabel);
		ALoginPageLabel = new JLabel("Enter password");
		ALoginPageInput = new JTextField();
		ALoginPageButton = new JButton("Login");
		ALoginPageMessage = new JLabel();
		ALoginPageLabel.setFont(new Font("",Font.PLAIN,18));
		ALoginPageInput.setFont(new Font("",Font.PLAIN,18));
		ALoginPageButton.setFont(new Font("",Font.BOLD,18));
		ALoginPageMessage.setFont(new Font("",Font.ITALIC,18));
		ALoginPageButton.addActionListener(this);
		f.add(ALoginPageLabel);
		f.add(ALoginPageButton);
		f.add(ALoginPageInput);
		f.add(ALoginPageMessage);
		ILoginMessage = new JLabel();
		HLoginMessage = new JLabel();
		ILoginMessage.setFont(new Font("",Font.ITALIC,18));
		HLoginMessage.setFont(new Font("",Font.ITALIC,18));
		f.add(ILoginMessage);
		f.add(HLoginMessage);
		ILoginButton = new JButton("Login");
		HLoginButton = new JButton("Login");
		ILoginButton.setFont(new Font("",Font.BOLD,18));
		HLoginButton.setFont(new Font("",Font.BOLD,18));
		ILoginButton.addActionListener(this);
		HLoginButton.addActionListener(this);
		f.add(ILoginButton);
		f.add(HLoginButton);
		ILoginPageTF1 = new JTextField();
		ILoginPageTF2 = new JTextField();
		HLoginPageTF1 = new JTextField();
		HLoginPageTF2 = new JTextField();
		ILoginPageLabel1 = new JLabel("Enter id : ");
		ILoginPageLabel2 = new JLabel("Enter Pin : ");
		HLoginPageLabel1 = new JLabel("Enter id : ");
		HLoginPageLabel2 = new JLabel("Enter Pin : ");
		ILoginPageTF1.setFont(new Font("",Font.PLAIN,18));
		ILoginPageTF2.setFont(new Font("",Font.PLAIN,18));
		HLoginPageTF1.setFont(new Font("",Font.PLAIN,18));
		HLoginPageTF2.setFont(new Font("",Font.PLAIN,18));
		ILoginPageLabel1.setFont(new Font("",Font.PLAIN,18));
		ILoginPageLabel2.setFont(new Font("",Font.PLAIN,18));
		HLoginPageLabel1.setFont(new Font("",Font.PLAIN,18));
		HLoginPageLabel2.setFont(new Font("",Font.PLAIN,18));
		f.add(ILoginPageTF1);
		f.add(ILoginPageTF2);
		f.add(HLoginPageTF1);
		f.add(HLoginPageTF2);
		f.add(ILoginPageLabel1);
		f.add(ILoginPageLabel2);
		f.add(HLoginPageLabel1);
		f.add(HLoginPageLabel2);
		requestDetailsPageLabel1[0].setText("Requester id : ");
		requestDetailsPageLabel1[1].setText("Requester name : ");
		requestDetailsPageLabel1[2].setText("Status : ");
		requestDetailsPageLabel1[3].setText("mobile number(s) of requester : ");
		requestDetailsPageLabel1[4].setText("Department : ");
		requestDetailsPageLabel1[5].setText("Date : ");
		requestDetailsPageLabel1[6].setText("Service(s) : ");
		requestDetailsPageLabel1[7].setText("Requestee id : ");
		requestDetailsPageLabel1[8].setText("Requestee name : ");
		requestDetailsPageLabel1[9].setText("Requestee mobile number(s)");
		requestDetailsPageLabel1[10].setText("Completed data : ");
		requestDetailsPageRequestNumberLabel = new JLabel("Enter request number : ");
		requestDetailsPageRequestNumberLabel.setFont(new Font("",Font.PLAIN,22));
		requestDetailsPageInput = new JTextField();
		requestDetailsPageInput.setFont(new Font("",Font.PLAIN,22));
		requestDetailsPageMessage = new JLabel();
		requestDetailsPageMessage.setFont(new Font("",Font.ITALIC,18));
		requestDetailsPageButton = new JButton("Get Details");
		requestDetailsPageButton.setFont(new Font("",Font.BOLD,18));
		requestDetailsPageButton.addActionListener(this);
		f.add(requestDetailsPageButton);
		f.add(requestDetailsPageMessage);
		f.add(requestDetailsPageInput);
		f.add(requestDetailsPageRequestNumberLabel);
		requestPageOkButton = new JButton("OK");
		backToHomeButton = new JButton("Go back to home page");
		f.add(requestPageOkButton);
		f.add(backToHomeButton);
		requestPageMessage = new JLabel();
		f.add(requestPageMessage);
		String Departments[] = {"CSE","ECE","EEE"};
		requestPageDepartmentComboBox = new JComboBox(Departments);
		requestPageMessage.setFont(new Font("",Font.ITALIC,18));
		requestPageOkButton.addActionListener(this);
		backToHomeButton.addActionListener(this);
		requestPageOkButton.setFont(new Font("",Font.BOLD,18));
		backToHomeButton.setFont(new Font("",Font.BOLD,18));
		requestPageLabels[0].setText("Enter Your Id");
		requestPageLabels[1].setText("Enter Services(seperated by ',')");
		requestPageLabels[2].setText("Select request department");
		homeButtons[0].setText("Place Request");
		homeButtons[1].setText("View request details");
		homeButtons[2].setText("Enter as ICTS Staff");
		homeButtons[3].setText("Enter as HOD");
		homeButtons[4].setText("Enter as Admin");
		homeButtons[5].setText("Exit");
		requestPageDepartmentComboBox.setFont(new Font("",Font.PLAIN,18));
		f.add(requestPageDepartmentComboBox);
		f.add(welcomeLabel);
		f.setSize(1400,900);
		f.setLayout(null);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		loadHomePage();
	}
	private void close() throws SQLException{
		if(rs!=null) {
			rs.close();
		}
		if(st!=null) {
			st.close();
		}
		if(con!=null) {
			con.close();
		}
		if(ps!=null) {
			ps.close();
		}
	}
	public void actionPerformed(ActionEvent e) {
		String clicked = e.getActionCommand();
		if(clicked == "Place Request") {
			loadRequestPlacingPage();
		}
		else if(clicked == "Exit") {
			f.dispose();
			try {
				close();
			}
			catch(Exception E) {
				System.out.println(E.getMessage());
			}
		}
		else if(clicked == "Go back to home page") {
			loadHomePage();
		}
		else if(clicked  == "View request details") {
			loadRequestDetailsPage();
		}
		else if(clicked == "Approve") {
			String k = approveRequestsInput.getText();
			String requestNumbers[] = k.split(",");
			Boolean error1 = false;
			Boolean error2 = false;
			for(int i=0;i<requestNumbers.length;i++) {
				for(int j = 0;j<requestNumbers[i].length();j++) {
					if(requestNumbers[i].charAt(j)>'9' || requestNumbers[i].charAt(j)<'0') error1 = true;
				}
				if(requestNumbers[i].length() == 0) error1 = true;
			}
			if(k.length() == 0) error1 = true;
			if(error1 == false) {
				for(int i=0;i<requestNumbers.length;i++) {
					try {
						int requestNo = Integer.parseInt(requestNumbers[i]);
						ps = con.prepareStatement("select count(*) from request where request_no = ?");
						ps.setInt(1, requestNo);
						if(getIntFromResultSet(ps.executeQuery()) == 0) {
							error2 = true;
						}
						else {
							ps = con.prepareStatement("select status from request where request_no = ?");
							ps.setInt(1, requestNo);
							rs = ps.executeQuery();
							rs.next();
							if(isEqual(rs.getString("status"),"WA") == false) {
								error2 = true;
							}
							if(error2 == false) {
								ps = con.prepareStatement("select department from request where request_no = ?");
								ps.setInt(1,requestNo);
								rs = ps.executeQuery();
								rs.next();
								if(isEqual(Integer.toString(currentHLogin),Integer.toString(rs.getInt("department"))) == false) {
									error2 = true;
								}
							}
						}
					}
					catch(Exception E) {
						System.out.println(E.getMessage());
					}
					
				}
			}
			if(error1 == true) {
				approveRequestsMessage.setText("Invalid id");
			}
			else if(error2 == true) {
				approveRequestsMessage.setText("id you provided does not match to our database");
			}
			else {
				department temp = new department();
				for(int i=0;i<requestNumbers.length;i++) {
					request temp1 = new request();
					int requestNo = Integer.parseInt(requestNumbers[i]);
					for(int j=0;j<allRequests.size();j++) {
						if(allRequests.elementAt(j).requestNo == requestNo) temp1 = allRequests.elementAt(j);
					}
					temp.allotJob(temp1);
				}
				loadApprovalPage(currentHLogin);
			}
		}
		else if(clicked == "Deny") {
			String k = approveRequestsInput.getText();
			String requestNumbers[] = k.split(",");
			Boolean error1 = false;
			Boolean error2 = false;
			for(int i=0;i<requestNumbers.length;i++) {
				for(int j = 0;j<requestNumbers[i].length();j++) {
					if(requestNumbers[i].charAt(j)>'9' || requestNumbers[i].charAt(j)<'0') error1 = true;
				}
				if(requestNumbers[i].length() == 0) error1 = true;
			}
			if(k.length() == 0) error1= true;
			if(error1 == false) {
				for(int i=0;i<requestNumbers.length;i++) {
					try {
						int requestNo = Integer.parseInt(requestNumbers[i]);
						ps = con.prepareStatement("select count(*) from request where request_no = ?");
						ps.setInt(1, requestNo);
						if(getIntFromResultSet(ps.executeQuery()) == 0) {
							error2 = true;
						}
						else {
							ps = con.prepareStatement("select status from request where request_no = ?");
							ps.setInt(1, requestNo);
							rs = ps.executeQuery();
							rs.next();
							if(isEqual(rs.getString("status"),"WA") == false) {
								error2 = true;
							}
							if(error2 == false) {
								ps = con.prepareStatement("select department from request where request_no = ?");
								ps.setInt(1,requestNo);
								rs = ps.executeQuery();
								rs.next();
								if(isEqual(Integer.toString(currentHLogin),Integer.toString(rs.getInt("department"))) == false) {
									error2 = true;
								}
							}
						}
					}
					catch(Exception E) {
						System.out.println(E.getMessage());
					}
					
				}
			}
			if(error1 == true) {
				approveRequestsMessage.setText("Invalid id");
			}
			else if(error2 == true) {
				approveRequestsMessage.setText("id you provided does not match to our database");
			}
			else {
				department temp = new department();
				for(int i=0;i<requestNumbers.length;i++) {
					int requestNo = Integer.parseInt(requestNumbers[i]);
					temp.deny(requestNo);
				}
				loadApprovalPage(currentHLogin);
			}
		}
		else if(clicked == "Login") {
			if(e.getSource() == ILoginButton) {				
				String Id = ILoginPageTF1.getText();
				Boolean error1 = false,error2 = false;
				for(int i=0;i<Id.length();i++) if(Id.charAt(i)>'9' || Id.charAt(i) <'0') error1 = true;
				if(Id.length() == 0) error1 = true;
				int id = 0;
				if(error1 == false) {
					id = Integer.parseInt(Id);
					try {
						ps = con.prepareStatement("select count(*) from ICTS_staff where id = ?");
						ps.setInt(1, id);
						if(getIntFromResultSet(ps.executeQuery()) == 0){
							error2 = true;
						}
					}
					catch(Exception E) {
						System.out.println(E.getMessage());
					}
				}
				if(error1 == true) {
					ILoginMessage.setText("Invalid id");
				}
				else if(error2 == true) {
					ILoginMessage.setText("Id you provided is not matching to our database");
				}
				else {
					loadProcessingPage(id);
				}
			}
			else if(e.getSource() == HLoginButton) {				
				String Id = HLoginPageTF1.getText();
				Boolean error1 = false,error2 = false;
				for(int i=0;i<Id.length();i++) if(Id.charAt(i)>'9' || Id.charAt(i) <'0') error1 = true;
				if(Id.length() == 0) error1 = true;
				int id = 0;
				if(error1 == false) {
					id = Integer.parseInt(Id);
					if(id<1 || id>3) error2= true;
				}
				if(error1 == true) {
					HLoginMessage.setText("Invalid id");
				}
				else if(error2 == true) {
					HLoginMessage.setText("Id you provided is not matching to our database");
				}
				else {
					loadApprovalPage(id);
				}
			}
			else if(e.getSource() == ALoginPageButton) {
				String pass = ALoginPageInput.getText();
				if(isEqual("java",pass) == false || pass.length()!=4) {
					ALoginPageMessage.setText("incorrect password");
				}
				else {
					loadAdminPage();
				}
			}
		}
		else if(clicked == "Get Details") {
			for(int i=0;i<11;i++) {
				requestDetailsPageLabel1[i].setBounds(0,0,0,0);
				requestDetailsPageLabel2[i].setBounds(0,0,0,0);
			}
			requestDetailsPageMessage.setText("");
			String id = requestDetailsPageInput.getText();
			Boolean error1 = false,error2 = false;
			for(int i=0;i<id.length();i++) if(id.charAt(i)>'9' || id.charAt(i)<'0') error1 = true;
			if(id.length() == 0) error1 = true;
			int Id = 0;
			if(error1 == false) {
				Id = Integer.parseInt(id);
				try {
					ps = con.prepareStatement("select count(*) from request where request_no = ?");
					ps.setInt(1,Id);
					if(getIntFromResultSet(ps.executeQuery()) == 0) {
						error2 = true;
					}
				}
				catch(Exception E) {
					System.out.println(E.getMessage());
				}
			}
			if(error1 == true) {
				requestDetailsPageMessage.setText("Invalid id");
			}
			else if(error2 == true) {
				requestDetailsPageMessage.setText("Id you provided does not match to our database");
			}
			else {
				try {
					ps = con.prepareStatement("select requested_by from request where request_no = ?");
					ps.setInt(1, Id);
					rs = ps.executeQuery();
					rs.next();
					int requestedBy = rs.getInt("requested_by");
					ps = con.prepareStatement("select name from person where id = ?");
					ps.setInt(1, requestedBy);
					rs = ps.executeQuery();
					rs.next();
					String requestedByName = rs.getString("name");
					ps = con.prepareStatement("select mobile_no from person_mobile where id = ?");
					ps.setInt(1, requestedBy);
					rs = ps.executeQuery();
					Vector <String> requestedByMobile = new Vector <String> ();
					while(rs.next()) {
						requestedByMobile.add(rs.getString("mobile_no"));
					}
					int index = 0;
					for(int i=0;i<allRequests.size();i++) {
						if(allRequests.elementAt(i).requestNo == Id) index = i;
					}
					String status = allRequests.elementAt(index).status;
					requestDetailsPageLabel2[0].setText(Integer.toString(requestedBy));
					requestDetailsPageLabel2[1].setText(requestedByName);
					requestDetailsPageLabel2[2].setText(status);
					String temp = "";
					for(int i=0;i<requestedByMobile.size();i++) {
						temp+=requestedByMobile.elementAt(i);
						if(i != requestedByMobile.size()-1) temp += ", ";
					}
					requestDetailsPageLabel2[3].setText(temp);
					temp = "";
					if(allRequests.elementAt(index).department == 1) temp = "CSE";
					else if(allRequests.elementAt(index).department == 2) temp = "ECE";
					else temp = "EEE";
					requestDetailsPageLabel2[4].setText(temp);
					temp = "";
					requestDetailsPageLabel2[5].setText(allRequests.elementAt(index).date);
					for(int i=0;i<allRequests.elementAt(index).services.length;i++) {
						temp += allRequests.elementAt(index).services[i];
						if(i!=allRequests.elementAt(index).services.length-1) temp += ", ";
					}
					requestDetailsPageLabel2[6].setText(temp);
					for(int i=0;i<=6;i++) {
						requestDetailsPageLabel1[i].setBounds(400,300+30*i,300,30);
						requestDetailsPageLabel2[i].setBounds(700,300+30*i,300,30);
					}
					if(isEqual("C",status) || isEqual("P",status)) {
						for(int i=7;i<10;i++) {
							requestDetailsPageLabel1[i].setBounds(400,300+30*i,300,30);
							requestDetailsPageLabel2[i].setBounds(700,300+30*i,300,30);
						}
						ps = con.prepareStatement("select processed_by from request where request_no = ?");
						ps.setInt(1, Id);
						rs = ps.executeQuery();
						rs.next();
						int processedBy = rs.getInt("processed_by");
						requestDetailsPageLabel2[7].setText(Integer.toString(processedBy));
						ps = con.prepareStatement("select name from person where id = ?");
						ps.setInt(1, processedBy);
						rs = ps.executeQuery();
						rs.next();
						String processedByName = rs.getString("name");
						requestDetailsPageLabel2[8].setText(processedByName);
						ps = con.prepareStatement("select mobile_no from person_mobile where id = ?");
						ps.setInt(1, processedBy);
						rs = ps.executeQuery();
						Vector <String> processedByMobile = new Vector <String> ();
						temp = "";
						while(rs.next()) {
							processedByMobile.add(rs.getString("mobile_no"));
						}
						for(int i=0;i<processedByMobile.size();i++) {
							temp+=processedByMobile.elementAt(i);
							if(i!=processedByMobile.size()-1) temp += ", ";
						}
						requestDetailsPageLabel2[9].setText(temp);
					}
					if(isEqual(status,"C")) {
						requestDetailsPageLabel1[10].setBounds(400,300+30*10,300,30);
						requestDetailsPageLabel2[10].setBounds(700,300+30*10,300,30);
						requestDetailsPageLabel2[10].setText(allRequests.elementAt(index).completedDate);
					}
				}
				catch(Exception E) {
					System.out.println(E.getMessage());
				}
			}
		}
		else if(clicked == "Enter as ICTS Staff") {
			loadICTSLoginPage();
		}
		else if(clicked == "Enter as HOD") { 
			loadHODLoginPage();
		}
		else if(clicked == "Enter as Admin") {
			loadAdminLoginPage();
		}
		else if(clicked == "OK") {
			if(e.getSource() == requestPageOkButton) {
				requestPageMessage.setText("");
				String id = requestPageInputs[0].getText();
				String services = requestPageInputs[1].getText();
				int Department = requestPageDepartmentComboBox.getSelectedIndex()+1;
				String Services[] ;
				Boolean error1 = false,error3 = false,error2 = false;
				for(int i=0;i<id.length();i++) if(id.charAt(i)<'0' || id.charAt(i)>'9') error1 = true;
				if(id.length() == 0) error1 = true;
				if(services.length() == 0) error2 = true;
				int Id = 0;
				if(error1 == false) {
					try {
						Id = Integer.parseInt(id);
						ps = con.prepareStatement("select count(*) from person where id = ?");
						ps.setInt(1,Id);
						rs = ps.executeQuery();
						if(getIntFromResultSet(rs) == 0) error3 = true;
					}
					catch(Exception E) {
						System.out.println(E.getMessage());
					}
				}
				if(error1 == true) {
					requestPageMessage.setText("Invalid Id");
				}
				else if(error2 == true) {
					requestPageMessage.setText("Services field cannot be empty");
				}
				else if(error3 == true){
					requestPageMessage.setText("Id does not match to our data base");
				}
				else {
					Services = services.split(",");
				    request Request = new request();
				    Request.requestedBy = Id;
				    Request.department = Department;
				    Request.status = "WA";
				    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				    Date today = Calendar.getInstance().getTime();   
				    Request.date = df.format(today);
				    Boolean isStaff = false;
				    Boolean isICTS = false;
				    Request.services = Services;
				    Request.requestNo = allRequests.size() + 1;
				    try {
					    ps = con.prepareStatement("select count(*) from staff where id = ?");
					    ps.setInt(1, Request.requestedBy);
					    if(getIntFromResultSet(ps.executeQuery()) > 0) isStaff = true;
					    ps = con.prepareStatement("select count(*) from ICTS_staff where id = ?");
					    ps.setInt(1,Request.requestedBy);
					    if(getIntFromResultSet(ps.executeQuery()) > 0) isICTS = true;
				    }
				    catch(Exception E) {
				    	System.out.println(E.getMessage());
				    }
				    if(isICTS == true || isStaff == true) {
				    	Request.approvalRequired = 'N';
				    }
				    else Request.approvalRequired = 'Y';
				    try {
				    	ps = con.prepareStatement("insert into request values(?,?,?,?,?,?,?,?)");
				    	ps.setInt(1, allRequests.size()+1);
				    	ps.setInt(2, Request.requestedBy);
				    	ps.setInt(8,Request.department);
				    	ps.setString(4, Request.status);
				        long millis=System.currentTimeMillis();  
				        java.sql.Date date=new java.sql.Date(millis);  
				    	ps.setDate(3,date);
				    	ps.setString(6, Character.toString(Request.approvalRequired));
				    	ps.setNull(7,java.sql.Types.NULL);
				    	ps.setNull(5,java.sql.Types.NULL);
				    	ps.executeUpdate();
				    	for(int i=0;i<Request.services.length;i++) {
				    		ps = con.prepareStatement("insert into services values(?,?)");
				    		ps.setInt(1, Request.requestNo);
				    		ps.setString(2, Request.services[i]);
				    		ps.executeUpdate();
				    	}
				    }
				    catch(Exception E) {
				    	System.out.println(E.getMessage());
				    }
				    if(Request.approvalRequired == 'N') {
				    	allDepartments[Request.department-1].allotJob(Request);
				    }
				    allRequests.add(Request);
				    requestPageMessage.setText("Your request is submitted successfully (Your request number : " + allRequests.size() + ")");
				}
			}
			else if(e.getSource() == processRequestsButton) {
				String k = processRequestsInput.getText();
				String requestNumbers[] = k.split(",");
				Boolean error1 = false;
				Boolean error2 = false;
				for(int i=0;i<requestNumbers.length;i++) {
					for(int j = 0;j<requestNumbers[i].length();j++) {
						if(requestNumbers[i].charAt(j)>'9' || requestNumbers[i].charAt(j)<'0') error1 = true;
					}
					if(requestNumbers[i].length() == 0) error1 = true;
				}
				if(k.length() == 0) error1 = true;
				if(error1 == false) {
					for(int i=0;i<requestNumbers.length;i++) {
						try {
							int requestNo = Integer.parseInt(requestNumbers[i]);
							ps = con.prepareStatement("select count(*) from request where request_no = ?");
							ps.setInt(1, requestNo);
							if(getIntFromResultSet(ps.executeQuery()) == 0) {
								error2 = true;
							}
							else {
								ps = con.prepareStatement("select status from request where request_no = ?");
								ps.setInt(1, requestNo);
								rs = ps.executeQuery();
								rs.next();
								if(isEqual(rs.getString("status"),"P") == false) {
									error2 = true;
								}
								if(error2 == false) {
									ps = con.prepareStatement("select processed_by from request where request_no = ?");
									ps.setInt(1,requestNo);
									rs = ps.executeQuery();
									rs.next();
									if(isEqual(Integer.toString(currentILogin),Integer.toString(rs.getInt("processed_by"))) == false) {
										error2 = true;
									}
								}
							}
						}
						catch(Exception E) {
							System.out.println(E.getMessage());
						}
						
					}
				}
				if(error1 == true) {
					processRequestsMessage.setText("Invalid id");
				}
				else if(error2 == true) {
					processRequestsMessage.setText("id you provided does not match to our database");
				}
				else {
					ictsStaff temp = new ictsStaff();
					if(error1 == false) {
						for(int i=0;i<requestNumbers.length;i++) {
							int requestNo = Integer.parseInt(requestNumbers[i]);
							temp.processRequest(requestNo);
						}
					}
					loadProcessingPage(currentILogin);
				}
			}
		}
	}
	public static void main(String[] args) {
		phase3 n = new phase3();
	}
}
