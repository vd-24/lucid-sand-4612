package com.masai.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.masai.DTO.Employee;
import com.masai.DTO.Leave;
import com.masai.DTO.LeaveImpl;
import com.masai.Exceptions.RecordNotFoundException;
import com.masai.Exceptions.SomeThingWentWrongException;

public class EmployeeOperationDAOImpl implements EmployeeOperationDAO{
	public void userLogIn(String username , String password) throws SomeThingWentWrongException {
		Connection con = null;
				
				try {
					con = DBUtils.getConnectionTodatabase();
					String query = "SELECT ID from Employee WHERE username='"+username+"' AND password='"+password+"' AND is_deleted=1";
					PreparedStatement ps = con.prepareStatement(query);
					
					ResultSet rs = ps.executeQuery();
					
					
					if(DBUtils.isResultSetEmpty(rs)) {
						throw new SomeThingWentWrongException("Invalid Credentials...");
					}else {
						rs.next();
						int empID = rs.getInt(1);
						UserLoggedIn.loggedInUser=empID;
					}
					
				} catch (ClassNotFoundException | SQLException e) {
					throw new SomeThingWentWrongException("Something went wrong");
				}
				try {
					DBUtils.closeConnection(con);
				} catch (SQLException e) {	}
				
	
		
	}
	
	public void logOut() {
		UserLoggedIn.loggedInUser=0;
	}

	@Override
	public void deleteAccount() throws SomeThingWentWrongException {
		Connection con = null;
		try {
			con=DBUtils.getConnectionTodatabase();
			String query = "UPDATE employee SET is_deleted=0 WHERE ID = "+UserLoggedIn.loggedInUser+"";
			PreparedStatement ps = con.prepareStatement(query);
			
			if(ps.executeUpdate()>0) {
				System.out.println("Account Deleted Successfully");
			}
			else {
				System.out.println("Not able to delete account");
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new SomeThingWentWrongException("Something went wrong..");
		}
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
		
	}

	@Override
	public void updateEmployeeName(Employee emp) throws SomeThingWentWrongException {
		Connection con = null;
		try {
			con=DBUtils.getConnectionTodatabase();
			String query = "UPDATE employee SET EmployeeName='"+emp.getEmployeeName()+"' WHERE ID = "+UserLoggedIn.loggedInUser+"";
			PreparedStatement ps = con.prepareStatement(query);
			
			if(ps.executeUpdate()>0) {
				System.out.println("Name Updated Successfully");
			}
			else {
				System.out.println("Not able to update name");
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new SomeThingWentWrongException("Something went wrong..");
		}
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
	}

	@Override
	public void updatePassword(Employee emp) throws SomeThingWentWrongException {
		Connection con = null;
		try {
			con=DBUtils.getConnectionTodatabase();
			String query = "UPDATE employee SET password='"+emp.getPassword()+"' WHERE ID = "+UserLoggedIn.loggedInUser+"";
			PreparedStatement ps = con.prepareStatement(query);
			
			if(ps.executeUpdate()>0) {
				System.out.println("Password Updated Successfully");
			}
			else {
				System.out.println("Not able to update password");
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new SomeThingWentWrongException("Something went wrong..");
		}
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
	}

	@Override
	public void updateEmployeeUsername(Employee emp) throws SomeThingWentWrongException{
		Connection con = null;
		try {
			con=DBUtils.getConnectionTodatabase();
			String query = "UPDATE employee SET username='"+emp.getUsername()+"' WHERE ID = "+UserLoggedIn.loggedInUser+"";
			PreparedStatement ps = con.prepareStatement(query);
			
			if(ps.executeUpdate()>0) {
				System.out.println("Username Updated Successfully");
			}
			else {
				System.out.println("Not able to update Username");
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new SomeThingWentWrongException("Something went wrong..");
		}
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
	}

	@Override
	public void applyForLeave(Leave leave) throws SomeThingWentWrongException {
		Connection con = null;
//		int empID;
//		LocalDate from;
//		LocalDate to;
//		String status;
//		int days;
//		String remark;
		try {
			con = DBUtils.getConnectionTodatabase();
			
			String query = "INSERT INTO leaves (EmployeeID,from_date,to_date,status,days,remark) VALUES (?,?,?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(query);
			
			ps.setInt(1, leave.getEmpID());
			ps.setDate(2,Date.valueOf(leave.getFrom()));
			ps.setDate(3,Date.valueOf(leave.getTo()));
			ps.setString(4, leave.getStatus());
			ps.setInt(5,leave.getDays());
			ps.setString(6, null);
			
			if(ps.executeUpdate()>0) {
				System.out.println("Applied for leave Successfully");
			}else {
				System.out.println("Not able to apply for the leave.");
			}
			
			  
		} catch (ClassNotFoundException | SQLException e) {
			throw new SomeThingWentWrongException("Something went wrong...");
		}
		
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
		
	}

	@Override
	public List<Leave> getLeavesHistory() throws SomeThingWentWrongException, RecordNotFoundException {
		List<Leave> list = new ArrayList<>();
		Connection con = null;
		
		try {
			con = DBUtils.getConnectionTodatabase();
			String query = "SELECT from_date, to_date, status, days, remark FROM Leaves WHERE employeeID = "+UserLoggedIn.loggedInUser+"";
			PreparedStatement ps = con.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			if(DBUtils.isResultSetEmpty(rs)) {
				throw new RecordNotFoundException("No record found");
			}else
			{
				while(rs.next()) {				
					list.add(new LeaveImpl(UserLoggedIn.loggedInUser,rs.getDate(1).toLocalDate(),rs.getDate(2).toLocalDate(),rs.getString(3),rs.getInt(4),rs.getString(5)));
				}
				
			}
			
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new SomeThingWentWrongException("Something went wrong");
		}
		
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {
			
		}
		return list;
		
	}

	@Override
	public int showSalaryPerMonth() throws RecordNotFoundException {
		Connection con = null;
		int salary=0;
		try {
			con = DBUtils.getConnectionTodatabase();
			String query = "SELECT salary_per_month FROM employee WHERE ID="+UserLoggedIn.loggedInUser+"";
			PreparedStatement ps = con.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			if(DBUtils.isResultSetEmpty(rs)) {
				throw new RecordNotFoundException("No Record Found");
			}else {
				rs.next();
				salary = rs.getInt(1);
			}
		} catch (ClassNotFoundException | SQLException e) {}
		
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
		return salary;
	}

	@Override
	public Leave getLeaveStatus() throws RecordNotFoundException {
		Connection con = null;
		Leave leave=null;
		try {
			con = DBUtils.getConnectionTodatabase();
			String query = "SELECT from_date, to_date, status, days, remark FROM Leaves WHERE EmployeeID = "+UserLoggedIn.loggedInUser+" ORDER BY from_date DESC LIMIT 1";
			
			PreparedStatement ps = con.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			if(DBUtils.isResultSetEmpty(rs)) {
				throw new RecordNotFoundException("No Record Found");
			}else {
					rs.next();
					Leave temp = new LeaveImpl(UserLoggedIn.loggedInUser,rs.getDate(1).toLocalDate(),rs.getDate(2).toLocalDate(),rs.getString(3),rs.getInt(4),rs.getString(5));
					 leave = temp;
				}

		} catch (ClassNotFoundException | SQLException e) {}
		
		try {
			DBUtils.closeConnection(con);
		} catch (SQLException e) {}
		return leave;
	}
}
