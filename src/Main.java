import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {

	Session session;

	public static void main(String[] args) {
		Main main = new Main();
		//main.addNewData();
		//main.printSchools();
		//main.printClasses();
		main.executeQueries();
		main.countSchoolsInDb();
		main.countStudents();
		main.printSchoolsWithMoreThanTwoClasses();
		main.printSchoolWithSelectedProfileeClass();
		main.updateSchoolAddressById(2);
		main.addTeacher();
		main.close();
	}

	private void addTeacher() {
		Teacher newTeacher = new Teacher();
		Scanner in = new Scanner(System.in);
		System.out.print("Please type in teacher's name: ");
		newTeacher.setName(in.nextLine());
		System.out.print("Please type in teacher's subject(s): ");
		newTeacher.setSubject(in.nextLine());
		printClasses();
		System.out.print("Please select id teacher's class: ");

		Query query = session.createQuery("from SchoolClass where id= :id");
		query.setLong("id", in.nextInt());
		SchoolClass fetchedSchoolClass = (SchoolClass) query.uniqueResult();

		Set<SchoolClass> newTeacherClasses = new HashSet<>();
		newTeacherClasses.add(fetchedSchoolClass);

		newTeacher.setClasses(newTeacherClasses);

		Transaction transaction = session.beginTransaction();
		session.save(newTeacher);
		transaction.commit();
	}

	private void updateSchoolAddressById(long searchValue) {
		Query query = session.createQuery("from School where id= :id");
		query.setLong("id", searchValue);
		School school = (School) query.uniqueResult();
		System.out.printf("Schools with ID:%o name: %s addres: %s\n", searchValue, school.getName(), school.getAddress());
		Scanner in = new Scanner(System.in);
		System.out.printf("Please type in the new address of %s: ", school.getName());
		school.setAddress(in.nextLine());
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
		System.out.printf("School updated ID:%o name: %s addres: %s\n", searchValue, school.getName(), school.getAddress());

	}

	private void printSchoolWithSelectedProfileeClass() {
		String hql ="SELECT s FROM School s INNER JOIN s.classes classes WHERE classes.profile = 'mat-fiz' AND classes.currentYear >= 2";
		Query query = session.createQuery(hql);
		List result = query.list();
		System.out.printf("Schools with mat-fiz: %s\n", result);
	}

	private void printSchoolsWithMoreThanTwoClasses() {
		String hql ="SELECT s FROM School s INNER JOIN s.classes classes GROUP BY s HAVING COUNT(classes.id) >= 2";
		Query query = session.createQuery(hql);
		List result = query.list();
		System.out.printf("Number of schools with more than two classes: %s\n", result);
}

	private void countStudents() {
		String hql = "SELECT COUNT(*) FROM Student";
		Query query = session.createQuery(hql);
		Object result = query.uniqueResult();
		System.out.printf("Number of students in the database: %s\n", result);
	}

	private void countSchoolsInDb() {
		String hql = "SELECT COUNT(*) FROM School";
		Query query = session.createQuery(hql);
		Object result = query.uniqueResult();
		System.out.printf("Number of schools in the database: %s\n", result);
	}

	private void executeQueries() {
		String hql = "FROM School WHERE name='UE'";
		Query query = session.createQuery(hql);
		List results = query.list();
		System.out.println(results);
		// delete transaction
		int counter = 0;
		Transaction transaction = session.beginTransaction();
		for (Object result : results) {
			session.delete(result);
			counter++;
		}
		transaction.commit();
		System.out.printf("%d elements deleted %s\n", counter, hql.toLowerCase());
	}

	public Main() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	public void close() {
		session.close();
		HibernateUtil.shutdown();
	}

	private void printSchools() {
		Criteria crit = session.createCriteria(School.class);
		List<School> schools = crit.list();

		System.out.println("### Schools");
		for (School s : schools) {
			System.out.println(s);
			for (SchoolClass sc : s.getClasses()) {
				System.out.println(sc);
				for (Student student : sc.getStudents()) {
					System.out.println(student);
				}
			}
		}
	}
	
	private void printClasses() {
		Criteria crit = session.createCriteria(SchoolClass.class);
		List<SchoolClass> classes = crit.list();

		System.out.println("### Classes");
		for (SchoolClass s : classes) {
			System.out.printf("%d %s\n", s.getId(), s);
		}
	}
	
	private void addNewData() {
		
		School newSchool = new School();
		newSchool.setName("UJ");
		newSchool.setAddress("Go��bia 24");
		
		SchoolClass newSchoolClass = new SchoolClass();
		newSchoolClass.setProfile("Biochemistry");
		newSchoolClass.setStartYear(2001);
		newSchoolClass.setCurrentYear(2018);
		
		Set<SchoolClass> newSchoolClassSet = new HashSet<>();
		newSchoolClassSet.add(newSchoolClass);
		
		Student newStudent = new Student();
		newStudent.setName("Anonymous");
		newStudent.setSurname("Anonymous");
		newStudent.setPesel("84215512255");
		
		Set<Student> newStudentsSet = new HashSet<>();
		newStudentsSet.add(newStudent);
		
		newSchoolClass.setStudents(newStudentsSet);
		newSchool.setClasses(newSchoolClassSet);;
		
		

		Transaction transaction = session.beginTransaction();
		session.save(newSchool); // gdzie newSchool to instancja nowej szkoly
		transaction.commit();

	}

	private void jdbcTest() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("org.sqlite.JDBC");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection("jdbc:sqlite:school.db", "", "");

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM schools";
			ResultSet rs = stmt.executeQuery(sql);

			// STEP 5: Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				String name = rs.getString("name");
				String address = rs.getString("address");

				// Display values
				System.out.println("Name: " + name);
				System.out.println(" address: " + address);
			}
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}// end jdbcTest

}
