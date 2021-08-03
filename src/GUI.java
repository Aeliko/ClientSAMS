import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class is for the main GUI displayed
 * 
 * @author Aero
 *
 */
public class GUI {
	private WebDriver driver;
	private JavascriptExecutor js;

	private String file_location = "";

	private static String table_tag = "table";
	private static String table_id = "scores";
	private static String row_tag = "tr";
	private static String cell_tag = "input";

	private JMenuBar menuBar = new JMenuBar();
	private JMenu control = new JMenu("Control");
	private JMenuItem exit = new JMenuItem("Exit");
	private JMenuItem help = new JMenuItem("Help");
	private JPanel main_panel = new JPanel(new BorderLayout());
	private JPanel button_panel = new JPanel();
	private JFrame frame = new JFrame();
	private JTextArea jta = new JTextArea();
	private JScrollPane jsp = new JScrollPane(jta);

	private JButton start = new JButton("Open Browser");
	private JButton insert = new JButton("Import");
	private JButton close = new JButton("Close Browser");

	/**
	 * This is the main function
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args) {

		// Risky operations are wrapped in try-catch block
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		FileInputStream fis = null;
		// Risky operations are wrapped in try-catch block
		try {
			fis = new FileInputStream("properties.txt");

			// Create a Properties object and load the properties.txt
			Properties properties = new Properties();
			properties.load(fis);

			table_tag = properties.getProperty("table_tag");
			table_id = properties.getProperty("table_id");
			row_tag = properties.getProperty("row_tag");
			cell_tag = properties.getProperty("cell_tag");
		} catch (FileNotFoundException e) {
			System.out.println("Warning: properties file not found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		GUI gui = new GUI();
		gui.go();
	}

	/**
	 * This is the function that runs and creates the GUI, controls the browser, and
	 * handles importing Excel files
	 */
	private void go() {
		main_panel.add(button_panel, BorderLayout.NORTH);
		main_panel.add(jsp, BorderLayout.CENTER);
		button_panel.add(start);
		button_panel.add(insert);
		button_panel.add(close);

		// Disable the other buttons
		insert.setEnabled(false);
		close.setEnabled(false);

		control.add(exit);
		menuBar.add(control);
		menuBar.add(help);

		frame.add(main_panel, BorderLayout.CENTER);
		jta.setEditable(false);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setTitle("ClientSAMS");
		frame.setSize(600, 200);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				// Check if driver exists
				if (driver != null) {
					// Check if browser is properly closed
					if (!(driver.toString().contains("null"))) {
						driver.quit();
					}
				}
			}
		});

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Disable itself while enabling the other 2 buttons
				start.setEnabled(false);
				insert.setEnabled(true);
				close.setEnabled(true);

				// Risky operations are wrapped in try-catch block
				try {
					System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
					driver = new ChromeDriver();
					js = (JavascriptExecutor) driver;

					// Direct the user to the WebSAMS webpage
					driver.get("https://localhost/login" + "");
					js.executeAsyncScript(
							"alert(\"ClientSAMS override: Enter login credentials and navigate to the page where you want to import the marks.\")");
				} catch (SessionNotCreatedException e1) {
					jta.append(e1.getMessage() + "\n");
					jta.append("The chromedriver.exe version does not match the version of Chrome installed\n"
							+ "Please download a suitable version at https://chromedriver.chromium.org/downloads");
					e1.printStackTrace();
				} catch (IllegalStateException e1) {
					jta.append(e1.getMessage() + "\n");
					// TODO: Add meaningful message for user
					e1.printStackTrace();
				} catch (Exception e1) {
					jta.append(e1.getMessage() + "\n");
					e1.printStackTrace();
				}
			}
		});

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Check if driver exists
				if (driver != null) {
					// Check if browser is properly closed
					if (!(driver.toString().contains("null"))) {
						driver.quit();
					}
				}

				// Enable the start button while disabling the other 2 buttons
				close.setEnabled(false);
				insert.setEnabled(false);
				start.setEnabled(true);
			}
		});

		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());

				// Only allow XLSX or XLS Files to be chosen
				// Excel Workbook (*.xlsx) Excel 97-2003 Workbook (*.xls)
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel 97-2003 Workbook (*.xls)", "xls"));
				int flag = chooser.showDialog(frame, "Import");

				// if the user selects a file
				if (flag == JFileChooser.APPROVE_OPTION) {
					file_location = chooser.getSelectedFile().getAbsolutePath();
					jta.append("Loaded file at \"" + file_location + "\"\n");

					// Risky operations are wrapped in try-catch block
					try {

						// Load the Excel Workbook
						Workbook wb = WorkbookFactory.create(new FileInputStream(file_location));
						// Load the Active Sheet
						Sheet sheet = wb.getSheet(wb.getSheetName(wb.getActiveSheetIndex()));
						// Create an ArrayList to hold the students
						ArrayList<Student> students = new ArrayList<Student>();
						jta.append("Reading Excel file...\n");

						// Initialize the students' name and marks
						for (Row row : sheet) {
							Student student = new Student();
							for (Cell cell : row) {
								// Check cell type
								switch (cell.getCellType()) {
									// If the type is STRING, then it should be the student's name
									case STRING:
										student.setName(cell.getStringCellValue());
										break;
									case NUMERIC:
										student.addMark((float) cell.getNumericCellValue());
										break;
									default:
										throw new IllegalArgumentException("Unexpected value: " + cell.getCellType());
								}
							}
							// Add the student to the students ArrayList
							students.add(student);
							jta.append(student.toString() + "\n");
							System.out.println(student.toString());
						}
						// Close the workbook
						wb.close();

						WebDriverWait wait = new WebDriverWait(driver, 10);
						// Wait for the webpage to be fully loaded
						wait.until(presenceOfElementLocated(By.tagName(table_tag)));
						// Locate the table for entering marks
						WebElement mark_table = driver.findElement(By.id(table_id));

						// Get the rows of the table
						ArrayList<WebElement> trs = (ArrayList<WebElement>) mark_table
								.findElements(By.tagName(row_tag));
						System.out.println("Number of rows in the table: " + trs.size());
						jta.append("Number of rows in the table: " + trs.size() + "\n");

						// Create nested Array List of cells
						ArrayList<ArrayList<WebElement>> rows = new ArrayList<ArrayList<WebElement>>();
						for (WebElement tr : trs) {
							rows.add((ArrayList<WebElement>) tr.findElements(By.tagName(cell_tag)));
						}

						System.out.println("Number of cells per row in the table: " + rows.get(0).size());
						jta.append("Number of cells per row in the table: " + rows.get(0).size() + "\n");

						// Check if the data size obtained from Excel matches that of the table on the
						// webpage
						if ((students.size() == rows.size())) {
							for (int i = 0; i < students.size(); i++) {
								if (students.get(i).getMarks().size() != rows.get(i).size()) {
									throw new Exception("Student " + students.get(i).getName()
											+ " does not have the same number of marks as that of his/her row in the table.");
								}
							}
						} else {
							throw new Exception("The number of students imported do not match that of the table.");
						}

						for (int i = 0; i < rows.size(); i++) {
							ArrayList<Float> marks = students.get(i).getMarks();
							for (int j = 0; j < rows.get(i).size(); j++) {

								// Enter the marks
								rows.get(i).get(j).sendKeys(Float.toString(marks.get(j)));
							}
						}
						jta.append("Finished importing marks!\n");
						js.executeAsyncScript("alert(\"ClientSAMS override: Finished importing marks!\")");

					} catch (EncryptedDocumentException e1) {
						jta.append(e1.getMessage() + "\n");
						e1.printStackTrace();
					} catch (FileNotFoundException e1) {
						jta.append(e1.getMessage() + "\n");
						jta.append("File not found!\n");
						e1.printStackTrace();
					} catch (IOException e1) {
						jta.append(e1.getMessage() + "\n");
						e1.printStackTrace();
					} catch (TimeoutException e1) {
						jta.append(e1.getMessage() + "\n");
						jta.append("No tables are found!\n");
						js.executeAsyncScript("alert(\"ClientSAMS override: No tables are found!\")");
						e1.printStackTrace();
					} catch (NoSuchElementException e1) {
						jta.append(e1.getMessage() + "\n");
						jta.append("Score input table not found!\n");
						js.executeAsyncScript("alert(\"ClientSAMS override: Score input table not found!\")");
						e1.printStackTrace();
					} catch (Exception e1) {
						jta.append(e1.getMessage() + "\n");
						js.executeAsyncScript("var message = \'" + e1.getMessage()
								+ "\'; alert(\"ClientSAMS override: \" + message);");
						e1.printStackTrace();
					}
				}

			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Read the instructions.pdf");
			}
		});
	}
}
