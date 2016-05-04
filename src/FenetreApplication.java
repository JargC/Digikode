import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 * <b>Classe repr�sentant la fen�tre principale de l'application Digikode</b>
 * @author Jean Clemenceau
 * @version 0.8
 */
public class FenetreApplication extends JFrame implements ActionListener{
	
	/**
	 * Serialisation de la classe
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Le pilote JDBC pour acc�der � la base de donn�es
	 */
	private String pilote = "com.mysql.jdbc.Driver";
	
    private JPanel pan = new JPanel();
    
    ///// Squelette de la table //////
    
	Object[][] data = {};  // les donn�es de la table, vides � cet instant
 		
	private String[] headers ={"Id","Date","Sport","De","�","Salle","Digicode",""}; // ent�tes des colonnes
		 
	DefaultTableModel model2 = new  DefaultTableModel(data,headers);	 // mod�lisation de l'ensemble de donn�es contenues par la table    		
		
	@SuppressWarnings("serial")
	private JTable table = new JTable(model2){
		public boolean isCellEditable(int row, int col) {		// on surcharge la m�thode isCellEditable afin
			if(col>=0&&col<7)									// d'empecher les modifications utilisateurs sur la table (sauf pour la colonne du bouton, autrement
			return false;										// elle serait inutilisable)
			else return true;
		}
	};; // affectation du modele � la table
	
	//////////////////////////////////
    
    /**
     * Constructeur de la fen�tre
     * @param login
     * @throws IOException
     */
    public FenetreApplication(String login) throws IOException {
    	   
    this.setTitle(login);
    this.setSize(700,500);
    setIconImage(ImageIO.read(new File("res/icone.png")));
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(null);
    
    pan.setLayout(new BorderLayout());
    
    JLabel label = new JLabel("Digikode - G�n�rateur de digicodes pour la M2L");
    label.setHorizontalAlignment(JLabel.CENTER);
    label.setVerticalAlignment(JLabel.CENTER);
    
    pan.add(label, BorderLayout.NORTH); 
                     
    setContentPane(pan);
    
    createReservationsTable(login);
        
    this.setVisible(true);   
       
 }
    
    /**
     * M�thode permettant de r�cup�rer la date du jour
     * @return la date du jour sous le meme format que celui de la base de donn�es
     */
    private String getDateJour(){
    	
     	String format = "yyyy-MM-dd";  // Le format souhait� pour la date

     	java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(format); // L'objet de formatage
     	java.util.Date date = new java.util.Date(); // On r�cup�re la date du jour

     	String dateJour = formater.format(date); // On la formate
     	
     	return dateJour;
     	
    }
    
    
    /**
     * M�thode cr�ant un digicode al�atoire, le mettant � jour dans la base de donn�es ainsi que dans le tableau de l'application
     * @param id
     * @param modelRow
     */
    public void createDigicode(String id, int modelRow) {
    	
    	String possibilites = "AB0123456789";		// les symboles utilisables sur  l'interface de digicode
    	String digicode = "";
    	Random r = new Random();					// objet pour g�n�rer la part d'aleatoire dans la construction de nos digicodes
    	
    	for (int i = 0; i < 5; i++) {				// i correspond � la longueur du digicode souhait�e
	    		int number = r.nextInt(11);			// on prend un entier compris entre 0 et 11
	    		digicode += possibilites.charAt(number);
		}    	
    	
    	String req = "Update reservation set digicode ='" + digicode + "' WHERE id ='" + id + "'";   
    	
    	
     	try{
     		Class.forName(pilote).newInstance();
     		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/agenda","root","");
     		Statement st = con.createStatement();
     		st.executeUpdate(req);
     		table.getModel().setValueAt(digicode, modelRow, 6);			// on met � jour la valeur dans le tableau pour un affichage imm�diat
     	}
     	
 		catch(Exception e1){
 			System.out.println(e1);
 		}
     	
    }
    
    
    /**
     * M�thode cr�ant le tableau r�capitulatif des r�servations de la personne
     * @param login
     */
    @SuppressWarnings("serial")
	private void createReservationsTable(String login) {  
    	
    	/////////////////////// R�cup�ration des donn�es ///////////////////////////////////
     	 	
     	String req = "SELECT * FROM reservation WHERE login = '"+login+"' AND date >= '"+getDateJour()+"'"; // On ne r�cup�re que les reservations � venir
     		
    	ResultSet res;
     	
     	try{
     		
     		Class.forName(pilote).newInstance();
     		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/agenda","root","");
     		Statement st = con.createStatement();
     		res = st.executeQuery(req); 
     		
     		if(res.next())
     		{
     				////////////////////////////////////// Creation de la table ///////////////////////////////////////    			
     			

	     		
	     		table.getTableHeader().setResizingAllowed(false); // on interdit le redimensionnement des colonnes
	     		table.getTableHeader().setReorderingAllowed(false); // on interdit le r�agencement des colonnes
	     		
	     		JScrollPane pane = new JScrollPane(table); 
	     		pane.setBounds(10,120,910,550);
	     		pane.setAutoscrolls(true);
	     		getContentPane().add(pane, BorderLayout.CENTER);
	     		pan.add(pane, BorderLayout.CENTER);
	     		
	     		
	     			////////////////////////////////////// Ajout des donn�es dans la table ///////////////////////////
	     		
	     		do{
	
	    				model2.addRow(new Object []{
	    						res.getString(1),// 
	    						res.getString(2),// 
	    						res.getString(3),//
	    						res.getString(4),//
	    						res.getString(5),//
	    						res.getString(6),
	    						res.getString(7),//
	    						"G�n�rer"
	    				});
	     		}
	     		
	     		while(res.next());
	     		
	            Action generer = new AbstractAction() {

	                @Override
	                public void actionPerformed(ActionEvent e) {							// action se r�alise lors d'un click sur un bouton "g�n�rer"
	                	table = (JTable) e.getSource();
	                	int modelRow = Integer.valueOf(e.getActionCommand());				// on recupere la ligne sujette
	                	String id = table.getModel().getValueAt(modelRow, 0).toString();	// on recupere l'id correspondant au futur traitement
	                	createDigicode(id, modelRow);										// on genere le digicode
	                }
	            };
	            
	            
	            ButtonColumn bc = new ButtonColumn(table, generer, 7); // on ajoute la colonne de boutons
	            bc.setMnemonic(KeyEvent.VK_D);

     		}
     		
     		else System.out.println("Pas de r�servations");    		
     		
    		}
     	
     		catch(Exception e1){
     			System.out.println(e1);
     		}
    	
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}