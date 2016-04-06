import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Interface2 extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pan = new JPanel();
    private JLabel label = new JLabel();
    
//    private String login_test;
//    public String getUserText() {
//        return login_test;
//    }
   
    
    public Interface2(String login_test)
 {
    pan.setLayout(new BorderLayout());
    this.setTitle("Mes réservations");
    this.setSize(700,500);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(null);
 
  //  pan.setBackground(Color.GRAY);
    
    
    
     
                     
    setContentPane(pan);
        
    this.setVisible(true); 
    
    	final String driver = "com.mysql.jdbc.Driver";
		final String url = "jdbc:mysql://localhost/agenda";
		final String user = "root";
		final String password = "";
		
   
		
     
 	System.out.println(login_test);
 	
 	String req = "SELECT * FROM reservation WHERE login ='"+login_test+"'";
		ResultSet res;
 	
 	try{
 		Class.forName(driver).newInstance();
 		Connection con = DriverManager.getConnection(url, user, password);
 		Statement st = con.createStatement();
 		res = st.executeQuery(req);
 		
 		if(res.next())
 		{
 		int i=0;
 		Object[][] DD = {};
 				final String[] EE ={"ID","Date","Sport","Heure Debut","Heure Fin"};
 		 
 		DefaultTableModel model2 = new  DefaultTableModel(DD,EE);
 		
 		JTable T = new JTable(model2);// l'affectation du model 
 		 
 		T.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 		JScrollPane P = new JScrollPane(T); 
 		P.setBounds(10,120,910,550);
 		P.setAutoscrolls(true);
 		getContentPane().add(P, BorderLayout.CENTER);
 		
 		
 		while(res.next())
 		{
 			i++;
				model2.addRow(new Object []{""+i,
						res.getString(1),// 
						res.getString(2),//
						res.getString(3),//
						res.getString(4),// 
				});
 		}
 		}else System.out.println("Pas de réservations");
 		
 		
 		
		}catch(Exception e1){
 			System.out.println("Classe non trouvée " + driver);
 		}
 }
}
