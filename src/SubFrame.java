import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class SubFrame extends JPanel{
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("My Computer"); 
	private JTree jtree_dir = new JTree(root);
	private TreePath curr_tp;
	private Vector f_header = new Vector(); // 제목줄
	private Vector f_data = new Vector(); // Data가 들어갈 vector 영역
	private JTable jtable_file; // Data를 표시할 테이블
	private JLabel info; // 파일의 정보 (이름, 경로) 를 출력할 라벨
	private JSplitPane p1;
	private JScrollPane scrollpane;
	private int selRow;
	private JPanel p2;
	private JTextArea context; //파일의 내용
	private String ssRow;
	private String fileName;
	private Vector<String> history_vector; // 히스토리를 저장하기위한 벡터
	private MainFrame f;
	private String last_path;
	private SubFrame sf;
	private JScrollPane s1;
	private TableRowSorter<TableModel> sorter;
	private int panel_state=0; // 패널 state,, 1=> 기본, 2=> 작은아이콘, 3=> 큰아이콘
	private boolean sort_state = true; // 오름차순 내림차순 하기위함, true => 오름차순, false => 내림차순
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem menu1 = new JMenuItem("오름차순");
	private JMenuItem menu2 = new JMenuItem("내림차순");

	SubFrame(MainFrame f){
		this.f = f;
		sf = this;
		setLayout(null);
		setBounds(0,0,MainFrame.screen_x, MainFrame.screen_y - (MainFrame.screen_y/ 20));

		history_vector = new Vector<String>(5);

		jtable_file = update_Table();
		jtable_file.addMouseListener(new MyTableListener());

		//수평으로 먼저 자름 
		JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp2.setBounds(10, MainFrame.screen_y / 20  , MainFrame.screen_x - 25,
				MainFrame.screen_y - MainFrame.screen_y / 20 - 70);

		JScrollPane scp1 = new JScrollPane(jtree_dir);
		sp2.setLeftComponent(scp1); // Tree를 왼쪽 컴퍼넌트에 붙임
		sp2.setDividerSize(5); // 나눔선 두꼐
		sp2.setDividerLocation(300); // 나눔선 초기 위치 
		initTree();
		// 오른쪽 패널을 수직으로 나눔 
		p1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		p1.setDividerLocation(500);
		p1.setDividerSize(5);
		p2 = new JPanel(); // txt 파일 Area;
		info = new JLabel();
		p2.setLayout(new BorderLayout());
		info.setText("열린파일 정보/// text 이름, 위치 등등");
		p2.add(info, BorderLayout.NORTH);
		context = new JTextArea("익스플로러 프로그램입니다. tree에서 드라이브를 선택해주세요. \n\n간단한 도움말을 보시려면 ctrl + h 를 누르세요");
		JScrollPane p123 = new JScrollPane(context);
		p2.add(p123, BorderLayout.CENTER);

		p1.setBottomComponent(p2);
		sp2.setRightComponent(p1);
		Print_RHS();
		add(sp2);
	}

	private void initTree() {
		f_header.add("File Name"); // 헤더 설정 1. 파일명
		f_header.add("File Type"); // 2. 파일 유형
		f_header.add("File Size (Bytes)"); // 3. 파일 크기
		f_header.add("Last Update"); // 4. 최종 수정일
		File[] dir = File.listRoots(); // 드라이브 항목을 가져온다
		for(int i = 0; i < dir.length; i++) { //드라이브 갯수만큼 돌면서
			DefaultMutableTreeNode drive = new DefaultMutableTreeNode(dir[i].toString().trim()); // 항목을 추가
			drive.add(new DefaultMutableTreeNode(null)); //아이콘이 폴더로 보이도록 더미 노드를 삽입한다
			root.add(drive); 

			Vector vc = new Vector(); // 트리에 폴더를 추가하면서 테이블 영역(파일 표시)에도 추가한다
			vc.add(dir[i].toString().trim());
			vc.add("<DRIVE>");  // 파일 사이즈 항목에 <DRIVE>라고 표시
			vc.add("");
			vc.add("");
			vc.add("");
			f_data.add(vc);
		}
		
		jtree_dir.expandRow(0);
		jtree_dir.addTreeWillExpandListener(new MyTreeWillExpandListener());
		jtree_dir.addMouseListener(new MytreeMouseListener());
	}



	public void viewFiles(TreePath selRow) {
		f_data.clear(); // 먼저 테이블의 내용을 삭제하고 폴더 내용으로 갱신
		ssRow = selRow.getLastPathComponent().toString().trim();

		System.out.println(ssRow);
		if(ssRow == null || ssRow.length() == 0) return;
		try {
			File cdir = new File(ssRow);
			File[] filelist = cdir.listFiles(); // 디렉토리 내용을 가져온다
			if(filelist.length == 0) { // 없으면 처리하고
				Vector vc = new Vector();
				vc.add("empty");
				vc.add("");
				vc.add("");
				vc.add("");
				f_data.add(vc);

				jtable_file = update_Table();
				jtable_file.addMouseListener(new MyTableListener());

				s1 = new JScrollPane(jtable_file);
				Print_RHS();
				jtable_file.addMouseListener(new MyTableListener());
				return; // 이벤트 종료
			}
			for (int i = 0; i < filelist.length; i++) { // 먼저 폴더를 표시하고
				if(filelist[i].isHidden()) continue;
				if(filelist[i].isDirectory()) {
					Vector vc = new Vector();
					vc.add(filelist[i].getName());
					vc.add("<DIR>");
					vc.add(0);
					vc.add(new Date(filelist[i].lastModified()).toString());
					f_data.add(vc);
				}
			}
			for (int i = 0; i < filelist.length; i++) { //파일을 나중에 표시한다
				if(filelist[i].isHidden()) continue;
				if(filelist[i].isFile()) {
					Vector vc = new Vector();
					vc.add(filelist[i].getName());
					vc.add(filelist[i].getName().substring(filelist[i].getName().lastIndexOf(".")+1) + " 파일");
					vc.add((int)filelist[i].length());
					vc.add(new Date(filelist[i].lastModified()).toString());
					f_data.add(vc);
				}
			}
			jtable_file = update_Table();

			s1 = new JScrollPane(jtable_file);
			Print_RHS();
			p1.setDividerLocation(500);
		} catch (NullPointerException ex) { // 오류 처리
			Vector vc = new Vector();
			vc.add("");
			vc.add("");
			vc.add("");
			vc.add("");
			f_data.add(vc);
			jtable_file = update_Table();

			p1.setDividerLocation(500);
			s1 = new JScrollPane(jtable_file);
			Print_RHS();
		}
	}
	private void viewFiles(String path) {
		f_data.clear();
		this.last_path = path;
		try {
			File cdir = new File(path);
			//			System.out.println("스트링으로 받은 메소드의 경로는 : " + path);
			File[] filelist = cdir.listFiles(); // 디렉토리 내용을 가져온다
			if(filelist.length == 0) { // 없으면 처리하고
				Vector vc = new Vector();
				vc.add("empty");
				vc.add("");
				vc.add("");
				vc.add("");
				f_data.add(vc);

				jtable_file = update_Table();
				jtable_file.addMouseListener(new MyTableListener());

				s1 = new JScrollPane(jtable_file);
				Print_RHS();
				jtable_file.addMouseListener(new MyTableListener());
				return; // 이벤트 종료
			}
			for (int i = 0; i < filelist.length; i++) { // 먼저 폴더를 표시하고
				if(filelist[i].isHidden()) continue;
				if(filelist[i].isDirectory()) {
					Vector vc = new Vector();
					vc.add(filelist[i].getName());
					vc.add("<DIR>");
					vc.add(0);
					vc.add(new Date(filelist[i].lastModified()).toString());
					f_data.add(vc);
				}
			}
			for (int i = 0; i < filelist.length; i++) { //파일을 나중에 표시한다
				if(filelist[i].isHidden()) continue;
				if(filelist[i].isFile()) {
					Vector vc = new Vector();
					vc.add(filelist[i].getName());
					vc.add(filelist[i].getName().substring(filelist[i].getName().lastIndexOf(".")+1) + " 파일");
					vc.add((int)filelist[i].length());
					vc.add(new Date(filelist[i].lastModified()).toString());
					f_data.add(vc);
				}
			}
			jtable_file = update_Table();

			s1 = new JScrollPane(jtable_file);
			Print_RHS();
			p1.setDividerLocation(500);
		} catch (NullPointerException ex) { // 오류 처리
			Vector vc = new Vector();
			vc.add("");
			vc.add("");
			vc.add("");
			vc.add("");
			f_data.add(vc);
			jtable_file = update_Table();

			p1.setDividerLocation(500);
			s1 = new JScrollPane(jtable_file);
			Print_RHS();
		}

	}

	JTable update_Table()
	{

		TableModel model = new DefaultTableModel(f_data, f_header) {
			public Class getColumnClass(int column) {
				if (column >= 0 && column <= getColumnCount())
					return getValueAt(0, column).getClass();
				else
					return Object.class;
			}
		};
		JTable new_table = new JTable(model){
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};;

		sorter = new TableRowSorter<TableModel>(model);
		new_table.setRowSorter(sorter);
		new_table.addMouseListener(new MyTableListener());
		this.panel_state=0;
		return new_table;
	}

	public void openFile(String path){

		String temp2 = path.replace("\\", "/");
		last_path = temp2;
		boolean history_check = true;

		for(int i = 0 ; i < history_vector.size() ; i++){
			if((history_vector.get(i).equals(path))){
				history_check = false;
				break;
			}
		}
		if((history_vector.size() < 5) && (history_check)){

			history_vector.add(path);
		}
		else if((history_vector.size() >= 5) && (history_check)){
			//			System.out.println("0번째 지워짐");
			history_vector.remove(0);
			history_vector.add(path);
		}
		Menu menu = new Menu(sf,history_vector);
		f.run(menu);

		String temp = temp2.substring(temp2.lastIndexOf("/") + 1)+ "   (" + temp2 + ")";
		info.setText(temp);
		context.setText(""); // edit 부분 초기화
		//파일 열기 메소드 

		StringBuffer temp3 = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(temp2));
			String line = null;
			while ((line = br.readLine()) != null) {
				temp3.append(String.valueOf(line) + '\n');
			}
			br.close();
		}
		catch (IOException e) {

		}
		context.setText(temp3.toString());
		Print_RHS();

	}

	public void Print_RHS()
	{
		if(panel_state==0)
		{   p1.removeAll();
		p1.setDividerLocation(500);
		p1.setDividerSize(10);
		JScrollPane p123 = new JScrollPane(s1);
		p1.setTopComponent(p123);
		p1.setBottomComponent(p2);}
		else if(panel_state==1)
		{
			p1.removeAll();
			p1.setDividerLocation(500);
			p1.setDividerSize(10);
			make_small_icon();
			p1.setBottomComponent(p2);
		}
		else if(panel_state==2)
		{
			p1.removeAll();
			p1.setDividerLocation(500);
			p1.setDividerSize(10);
			make_big_icon();
			p1.setBottomComponent(p2);
		}
	}

	public void make_small_icon()
	{
		final int items_per_line = p2.getWidth()/200;
		String filename="";
		int i;
		Vector cur_vector;
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.panel_state = 1;
		JLabel cur_bt = null;
		panel.setLayout(new GridLayout(p1.getHeight()/100,5));
		if(sort_state){
			for(i=0 ; i<f_data.size() ; i++)
			{
				cur_vector = (Vector) f_data.get(i);
				filename = cur_vector.get(0).toString();
				if(cur_vector.get(1).toString().equals("<DIR>"))
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("folder _32.png"), SwingConstants.LEFT);

				else if(filename.contains(".mp4") || filename.contains(".avi") 
						|| filename.contains(".mkv") || filename.contains(".mp3") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("multimedia.png"), SwingConstants.LEFT);

				else if(filename.contains(".jpg") || filename.contains(".png") 
						|| filename.contains(".gif") || filename.contains(".jpeg") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("image.png"), SwingConstants.LEFT);

				else if(filename.contains(".docx") || filename.contains(".doc") 
						|| filename.contains(".hwp") || filename.contains(".pdf") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("document.png"), SwingConstants.LEFT);

				else if(filename.contains(".exe") || filename.contains(".dmg") 
						|| filename.contains(".out") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("program.png"), SwingConstants.LEFT);
				else if(filename.contains(".zip") || filename.contains(".alz") 
						|| filename.contains(".7z") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("zip.png"), SwingConstants.LEFT);
				else
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("file _32.png"), SwingConstants.LEFT);
				cur_bt.setToolTipText(cur_vector.get(0).toString());
				cur_bt.setBackground(Color.WHITE);
				cur_bt.setPreferredSize(new Dimension(200, 50));
				cur_bt.setBounds(i%items_per_line*200, i/items_per_line*50, 200, 50);
				cur_bt.addMouseListener(new MyIconListener() );
				panel.add(cur_bt);
			}
		}
		else
		{
			for(i=0 ; i<f_data.size() ; i++)
			{
				cur_vector = (Vector) f_data.get(f_data.size()-i-1);
				filename = cur_vector.get(0).toString();
				if(cur_vector.get(1).toString().equals("<DIR>"))
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("folder _32.png"), SwingConstants.LEFT);
				else if(filename.contains(".mp4") || filename.contains(".avi") 
						|| filename.contains(".mkv") || filename.contains(".mp3") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("multimedia.png"), SwingConstants.LEFT);

				else if(filename.contains(".jpg") || filename.contains(".png") 
						|| filename.contains(".gif") || filename.contains(".jpeg") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("image.png"), SwingConstants.LEFT);

				else if(filename.contains(".docx") || filename.contains(".doc") 
						|| filename.contains(".hwp") || filename.contains(".pdf") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("document.png"), SwingConstants.LEFT);

				else if(filename.contains(".exe") || filename.contains(".dmg") 
						|| filename.contains(".out") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("program.png"), SwingConstants.LEFT);
				else if(filename.contains(".zip") || filename.contains(".alz") 
						|| filename.contains(".7z") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("zip.png"), SwingConstants.LEFT);

				else
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("file _32.png"), SwingConstants.LEFT);
				cur_bt.setToolTipText(cur_vector.get(0).toString());
				cur_bt.setBackground(Color.WHITE);
				cur_bt.setPreferredSize(new Dimension(200, 50));
				cur_bt.setBounds(i%items_per_line*200, i/items_per_line*50, 200, 50);
				cur_bt.addMouseListener(new MyIconListener() );
				panel.add(cur_bt);
			}
		}
		this.panel_state=1;
		JScrollPane sp = new JScrollPane(panel);
		p1.setTopComponent(sp);
	}
	public void make_small_icon(String s)
	{
		/*****************items_per_line :: 한 줄에 몇개??? ********/
		final int items_per_line = p2.getWidth()/200;
		int i;
		Vector cur_vector = null;
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.panel_state = 1;
		JLabel cur_bt = null;
		panel.setLayout(new GridLayout(p1.getHeight()/100,5));
		if(sort_state){
			for(i=0 ; i<f_data.size() ; i++)
			{

				cur_vector = (Vector) f_data.get(i);
				if(cur_vector.get(0).toString().contains(s)){
					if(cur_vector.get(1).toString().equals("<DIR>"))
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("folder _32.png"), SwingConstants.LEFT);
					else
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("file _32.png"), SwingConstants.LEFT);
					cur_bt.setToolTipText(cur_vector.get(0).toString());
					cur_bt.setBackground(Color.WHITE);
					cur_bt.setPreferredSize(new Dimension(200, 50));
					cur_bt.setBounds(i%items_per_line*200, i/items_per_line*50, 200, 50);
					cur_bt.addMouseListener(new MyIconListener() );
					panel.add(cur_bt);
				}
			}
		}
		else
		{
			for(i=0 ; i<f_data.size() ; i++)
			{
				cur_vector = (Vector) f_data.get(f_data.size()-i-1);
				if(cur_vector.get(0).toString().contains(s)){
					if(cur_vector.get(1).toString().equals("<DIR>"))
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("folder _32.png"), SwingConstants.LEFT);
					else if(cur_vector.get(1).toString().contains(s))
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("file _32.png"), SwingConstants.LEFT);
					cur_bt.setToolTipText(cur_vector.get(0).toString());
					cur_bt.setBackground(Color.WHITE);
					cur_bt.setPreferredSize(new Dimension(200, 50));
					cur_bt.setBounds(i%items_per_line*200, i/items_per_line*50, 200, 50);
					cur_bt.addMouseListener(new MyIconListener() );
					panel.add(cur_bt);
				}
			}
		}
		this.panel_state=1;
		JScrollPane sp = new JScrollPane(panel);
		p1.setTopComponent(sp);
		p1.setDividerLocation(500);
	}

	public void make_big_icon()
	{
		final int items_per_line = p2.getWidth()/150;
		int i;
		String filename = "";
		Vector cur_vector;
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.panel_state = 2;
		JLabel cur_bt = null;
		panel.setLayout(new GridLayout(p1.getHeight()/300,10));
		if(sort_state){
			for(i=0; i<f_data.size(); i++)
			{
				cur_vector = (Vector) f_data.get(i);
				filename = cur_vector.get(0).toString();
				if(cur_vector.get(1).toString().equals("<DIR>"))
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("folder_large.png"), SwingConstants.CENTER);
				else if(filename.contains(".mp4") || filename.contains(".avi") 
						|| filename.contains(".mkv") || filename.contains(".mp3") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("multimedia_big.png"), SwingConstants.LEFT);

				else if(filename.contains(".jpg") || filename.contains(".png") 
						|| filename.contains(".gif") || filename.contains(".jpeg") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("image_big.png"), SwingConstants.LEFT);

				else if(filename.contains(".docx") || filename.contains(".doc") 
						|| filename.contains(".hwp") || filename.contains(".pdf") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("document_big.png"), SwingConstants.LEFT);

				else if(filename.contains(".exe") || filename.contains(".dmg") 
						|| filename.contains(".out") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("program_big.png"), SwingConstants.LEFT);
				else if(filename.contains(".zip") || filename.contains(".alz") 
						|| filename.contains(".7z") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("zip_big.png"), SwingConstants.LEFT);

				else
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("file_large.png"), SwingConstants.CENTER);
				cur_bt.setVerticalTextPosition(JLabel.BOTTOM);
				cur_bt.setHorizontalTextPosition(JLabel.CENTER);
				cur_bt.setToolTipText(cur_vector.get(0).toString());

				cur_bt.setBackground(Color.WHITE);
				cur_bt.setBounds(i%items_per_line*150, i/items_per_line*150, 150, 150);
				cur_bt.addMouseListener(new MyIconListener() );
				panel.add(cur_bt);
			}
		}
		else{
			for(i=0; i<f_data.size(); i++)
			{
				cur_vector = (Vector) f_data.get(f_data.size()-i-1);
				filename = cur_vector.get(0).toString();
				if(cur_vector.get(1).toString().equals("<DIR>"))
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("folder_large.png"), SwingConstants.CENTER);
				else if(filename.contains(".mp4") || filename.contains(".avi") 
						|| filename.contains(".mkv") || filename.contains(".mp3") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("multimedia_big.png"), SwingConstants.LEFT);

				else if(filename.contains(".jpg") || filename.contains(".png") 
						|| filename.contains(".gif") || filename.contains(".jpeg") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("image_big.png"), SwingConstants.LEFT);

				else if(filename.contains(".docx") || filename.contains(".doc") 
						|| filename.contains(".hwp") || filename.contains(".pdf") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("document_big.png"), SwingConstants.LEFT);

				else if(filename.contains(".exe") || filename.contains(".dmg") 
						|| filename.contains(".out") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("program_big.png"), SwingConstants.LEFT);
				else if(filename.contains(".zip") || filename.contains(".alz") 
						|| filename.contains(".7z") )
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("zip_big.png"), SwingConstants.LEFT);

				else
					cur_bt = new JLabel(cur_vector.get(0).toString(), 
							new ImageIcon("file_large.png"), SwingConstants.CENTER);
				cur_bt.setVerticalTextPosition(JLabel.BOTTOM);
				cur_bt.setHorizontalTextPosition(JLabel.CENTER);
				cur_bt.setToolTipText(cur_vector.get(0).toString());
				cur_bt.setBackground(Color.WHITE);
				cur_bt.setBounds(i%items_per_line*150, i/items_per_line*150, 150, 150);
				cur_bt.addMouseListener(new MyIconListener() );
				panel.add(cur_bt);
			}
		}

		this.panel_state=2;
		JScrollPane sp = new JScrollPane(panel);
		p1.setDividerLocation(500);
		p1.setTopComponent(sp);
	}



	public void make_big_icon(String s)
	{
		/*****************items_per_line :: 한 줄에 몇개??? ********/
		final int items_per_line = p2.getWidth()/150;
		int i;
		Vector cur_vector;
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.panel_state = 2;
		JLabel cur_bt = null;
		panel.setLayout(new GridLayout(p1.getHeight()/300,10));
		if(sort_state){
			for(i=0; i<f_data.size(); i++)
			{
				cur_vector = (Vector) f_data.get(i);
				if(cur_vector.get(0).toString().contains(s)){
					if(cur_vector.get(1).toString().equals("<DIR>"))
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("folder_large.png"), SwingConstants.CENTER);

					else
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("file_large.png"), SwingConstants.CENTER);
					cur_bt.setVerticalTextPosition(JLabel.BOTTOM);
					cur_bt.setHorizontalTextPosition(JLabel.CENTER);
					cur_bt.setToolTipText(cur_vector.get(0).toString());

					cur_bt.setBackground(Color.WHITE);
					cur_bt.setBounds(i%items_per_line*150, i/items_per_line*150, 150, 150);
					cur_bt.addMouseListener(new MyIconListener() );
					panel.add(cur_bt);
				}
			}
		}
		else{
			for(i=0; i<f_data.size(); i++)
			{
				cur_vector = (Vector) f_data.get(f_data.size()-i-1);
				if(cur_vector.get(0).toString().contains(s)){
					if(cur_vector.get(1).toString().equals("<DIR>"))
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("folder_large.png"), SwingConstants.CENTER);

					else
						cur_bt = new JLabel(cur_vector.get(0).toString(), 
								new ImageIcon("file_large.png"), SwingConstants.CENTER);
					cur_bt.setVerticalTextPosition(JLabel.BOTTOM);
					cur_bt.setHorizontalTextPosition(JLabel.CENTER);
					cur_bt.setToolTipText(cur_vector.get(0).toString());
					cur_bt.setBackground(Color.WHITE);
					cur_bt.setBounds(i%items_per_line*150, i/items_per_line*150, 150, 150);
					cur_bt.addMouseListener(new MyIconListener() );
					panel.add(cur_bt);
				}
			}
		}

		this.panel_state=2;
		JScrollPane sp = new JScrollPane(panel);
		p1.setTopComponent(sp);
		p1.setDividerLocation(500);
	}
	public void Sort_Table(String s)
	{
		//단순히 이름만 검색
		f_data.clear();
		File fs = new File(getPath());
		File[] fs2 = fs.listFiles(); // 디렉토리 내용을 가져온다

		for(int i = 0 ; i < fs2.length ; i++){
			if(fs2[i].isDirectory()){
				if(fs2[i].getName().contains(s)){
					Vector vc = new Vector();
					vc.add(fs2[i].getName());
					vc.add("<DIR>");
					vc.add((int)fs2[i].length());
					vc.add(new Date(fs2[i].lastModified()).toString());
					f_data.add(vc);
				}
			}
		}

		for(int i = 0 ; i < fs2.length ; i++){
			if(fs2[i].isFile()){
				if(fs2[i].getName().contains(s)){
					Vector vc = new Vector();
					vc.add(fs2[i].getName());
					vc.add(fs2[i].getName().substring(fs2[i].getName().lastIndexOf(".")+1) + " 파일");
					vc.add((int)fs2[i].length());
					vc.add(new Date(fs2[i].lastModified()).toString());
					f_data.add(vc);
				}
			}
		}

		if(this.panel_state==0){
			jtable_file = update_Table();
			s1 = new JScrollPane(jtable_file);
			Print_RHS();
			p1.setDividerLocation(500);
		}
		else if(this.panel_state==1){
			make_small_icon(s);

		}
		else if(this.panel_state==2){
			make_big_icon(s);
		}
	}

	public void Sort_Table(String s, String s2, int search_state)
	{
		f_data.clear();
		File fs = new File(getPath());
		File[] fs2 = fs.listFiles(); // 디렉토리 내용을 가져온다
		if(search_state == 1){
			// 크기 지정 검색 , 폴더는 안함, 파일만
			for(int i = 0 ; i < fs2.length ; i++){
				if(fs2[i].isDirectory()){
					continue;
				}
				if(fs2[i].length() >= Integer.valueOf(s) &&
						fs2[i].length() <= Integer.valueOf(s2)){
					Vector vc = new Vector();
					vc.add(fs2[i].getName());
					vc.add(fs2[i].getName().substring(fs2[i].getName().lastIndexOf(".")+1) + " 파일");
					vc.add((int)fs2[i].length());
					vc.add(new Date(fs2[i].lastModified()).toString());
					f_data.add(vc);
				}
			}

			if(panel_state == 0){
				jtable_file = update_Table();
				s1 = new JScrollPane(jtable_file);
				Print_RHS();
				p1.setDividerLocation(500);
			}
			else if(panel_state ==1){
				make_small_icon();
			}
			else if(panel_state == 2){
				make_big_icon();
			}

		}
		else if(search_state == 2){
			//파일 타입을 검색하므로, 디렉토리는 통과, txt,pdf 등등 만 검색
			for(int i = 0 ; i < fs2.length ; i++){
				if(fs2[i].isDirectory()){
					continue;
				}
				if(fs2[i].getName().substring(fs2[i].getName().lastIndexOf(".")+1).equals(s)){
					Vector vc = new Vector();
					vc.add(fs2[i].getName());
					vc.add(fs2[i].getName().substring(fs2[i].getName().lastIndexOf(".")+1) + " 파일");
					vc.add((int)fs2[i].length());
					vc.add(new Date(fs2[i].lastModified()).toString());
					f_data.add(vc);
				}				
			}
			if(panel_state == 0){
				jtable_file = update_Table();
				s1 = new JScrollPane(jtable_file);
				Print_RHS();
				p1.setDividerLocation(500);
			}
			else if(panel_state ==1){
				make_small_icon();
			}
			else if(panel_state == 2){
				make_big_icon();
			}
		}
		else if(search_state == 3){
			for(int i = 0 ; i < fs2.length ; i++){
				if(fs2[i].isDirectory()){
					String temp5 = new Date(fs2[i].lastModified()).toString();
					if(temp5.contains(s)){
						Vector vc = new Vector();
						vc.add(fs2[i].getName());
						vc.add(fs2[i].getName().substring(fs2[i].getName().lastIndexOf(".")+1) + " 파일");
						vc.add((int)fs2[i].length());
						vc.add(new Date(fs2[i].lastModified()).toString());
						f_data.add(vc);
					}				
				}
			}
			for(int i = 0 ; i < fs2.length ; i++){
				if(fs2[i].isFile()){
					String temp5 = new Date(fs2[i].lastModified()).toString();
					if(temp5.contains(s)){
						Vector vc = new Vector();
						vc.add(fs2[i].getName());
						vc.add(fs2[i].getName().substring(fs2[i].getName().lastIndexOf(".")+1) + " 파일");
						vc.add((int)fs2[i].length());
						vc.add(new Date(fs2[i].lastModified()).toString());
						f_data.add(vc);
					}				
				}
			}
			if(panel_state == 0){
				jtable_file = update_Table();
				s1 = new JScrollPane(jtable_file);
				Print_RHS();
				p1.setDividerLocation(500);
			}
			else if(panel_state ==1){
				make_small_icon();
			}
			else if(panel_state == 2){
				make_big_icon();
			}

		}
	}

	private class MytreeMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) { // 싱글/더블 클릭 이벤트 인가? 여기서는 테이블만 변경 함
			if(e.getSource() == jtree_dir) { //트리에서 발생했나?
				int selRow = jtree_dir.getRowForLocation(e.getX(), e.getY()); // 트리의 순번
				TreePath selPath = jtree_dir.getPathForLocation(e.getX(), e.getY()); // 선택된 트리의 경로
				//				System.out.println(selPath);
				//				System.out.println("x = "+e.getX());
				//				System.out.println("y = "+e.getY());

				if(selRow == 0) { // Root를 클릭하면 우측 테이블에 드라이브를 표시하도록 함
					f_data.clear(); // 먼저 테이블의 내용을 삭제하고 드라이브 추가
					File[] dir = File.listRoots(); // 드라이브 항목을 가져온다. 위의 init() 와 동일하나 트리는 변경하지 않음
					for(int i = 0; i < dir.length; i++) { //테이블만 갱신
						Vector vc = new Vector();
						vc.add(dir[i].toString().trim());
						vc.add("<DRIVE>");
						vc.add("");
						f_data.add(vc);
					}
					TableModel model = new DefaultTableModel(f_data, f_header) {
						public Class getColumnClass(int column) {
							if (column >= 0 && column <= getColumnCount())
								return getValueAt(0, column).getClass();
							else
								return Object.class;
						}
					};
					JTable jtable_file = new JTable(model){
						public boolean isCellEditable(int row, int column) {
							return false;
						}
					};;
					sorter = new TableRowSorter<TableModel>(model);
					jtable_file.setRowSorter(sorter);
					jtable_file.addMouseListener(new MyTableListener());

					s1 = new JScrollPane(jtable_file);
					Print_RHS();
					p1.setDividerLocation(500);


				} else if(selRow != -1) { // 다른 트리 항목을 클릭하면
					if(curr_tp != selPath) { //현재 테이블 폴더와 클릭한 폴더가 다른 경우에만
						if(panel_state == 0){
							viewFiles(selPath); // 테이블을 갱신
							curr_tp = selPath; // 현재 테이블에 표시된 폴더 기억 
						}
						else if(panel_state ==1){
							viewFiles(selPath); // 테이블을 갱신
							make_small_icon();
							curr_tp = selPath; // 현재 테이블에 표시된 폴더 기억 
						}
						else if(panel_state == 2){
							viewFiles(selPath); // 테이블을 갱신
							make_big_icon();
							curr_tp = selPath; // 현재 테이블에 표시된 폴더 기억 
						}
					}
				}
			}
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

	}

	private class MyTableListener implements MouseListener{
		//      String path = null;
		int cnt = 0;
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getClickCount() == 2)
			{
				JTable table = (JTable)e.getSource();
				int row = table.getSelectedRow();
				fileName = (String) table.getValueAt(row, 0);
				String flag = (String) table.getValueAt(row, 1);
				String path = getPath() +"/" +fileName;
				File f2 = new File(path);
				last_path = path;
				if(f2.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "폴더입니다.");
				}
				else 
				{
					// 만약 txt이 포함 되어있고, dir이 아니라면
					String temp = fileName + "   (" + getPath() + ")";
					info.setText(temp);

					openFile(path);
					// 백터 자료구조에 histroy를 저장함.
					boolean history_check = true;

					for(int i = 0 ; i < history_vector.size() ; i++){
						if((history_vector.get(i).equals(path))){
							history_check = false;
							break;
						}
					}
					if((history_vector.size() < 5) && (history_check)){

						history_vector.add(path);
					}
					else if((history_vector.size() >= 5) && (history_check)){
						//						System.out.println("0번째 지워짐");
						history_vector.remove(0);
						history_vector.add(path);
					}
					Menu menu = new Menu(sf,history_vector);
					f.run(menu);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}


	}

	private class MyTreeWillExpandListener implements TreeWillExpandListener{

		@Override
		public void treeWillCollapse(TreeExpansionEvent event)
				throws ExpandVetoException {
			// TODO Auto-generated method stub

		}

		@Override
		public void treeWillExpand(TreeExpansionEvent e)
				throws ExpandVetoException {
			// TODO Auto-generated method stub
			if(e.getSource() == jtree_dir) { 
				TreePath selRow = e.getPath();
				String ssRow = selRow.getLastPathComponent().toString().trim();
				if(ssRow == null || ssRow.length() == 0) return;
				DefaultMutableTreeNode temp = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				temp.removeAllChildren(); // 트리가 미리 가지고 있던 모든 노드를 지운다
				boolean direxist = false;
				try {
					File cdir = new File(ssRow); 
					File[] dirlist = cdir.listFiles(); // 폴더내의 디렉토리를 가져온다
					for (int i = 0; i < dirlist.length; i++) { // 모든 리스트에 대해서
						if(dirlist[i].isHidden()) continue; // 감춤 속성 제외
						if(dirlist[i].isDirectory()) { // 폴더에 대해서만
							DefaultMutableTreeNode d = new DefaultMutableTreeNode(dirlist[i].toString().trim());
							d.add(new DefaultMutableTreeNode(null));
							temp.add(d); // 트리에 추가 한다
							direxist = true;
						}
					}
					if(direxist == false) { // 만약 폴더가 없으면
						temp.add(new DefaultMutableTreeNode("Empty"));
						jtree_dir.collapsePath(selRow); 
					}
				} catch (NullPointerException ex) {
					temp.add(new DefaultMutableTreeNode("Empty")); //표시할 것이 없다고 알림
					jtree_dir.collapsePath(selRow);
				}

			}

		}

	}




	private class MyIconListener extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

			if(e.getClickCount() == 2)
			{
				JLabel btn = (JLabel) e.getSource();
				String path = getPath() +"/" +btn.getText();
				File f2 = new File(path);
				last_path = path;
				//				if(!path.contains("."))
				if(f2.isDirectory())
				{
					// 드라이브나, 폴더(DIR)을 클릭하였을때
					JOptionPane.showMessageDialog(null, "폴더입니다.");
				}
				else
				{
					String temp = fileName + "   (" + getPath() + ")";
					info.setText(temp);

					openFile(path);
					// 백터 자료구조에 histroy를 저장함.
					boolean history_check = true;

					for(int i = 0 ; i < history_vector.size() ; i++){
						if((history_vector.get(i).equals(path))){
							history_check = false;
							break;
						}
					}
					if((history_vector.size() < 5) && (history_check)){

						history_vector.add(path);
					}
					else if((history_vector.size() >= 5) && (history_check)){
						//						System.out.println("0번째 지워짐");
						history_vector.remove(0);
						history_vector.add(path);
					}
					Menu menu = new Menu(sf,history_vector);
					f.run(menu);
				}
			}
			if(e.getButton() == 3){
				menu1.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						sort_state = true;
						System.out.println(panel_state);
						if(panel_state == 1){
							make_small_icon();
						}
						else if(panel_state == 2)
						{
							make_big_icon();
						}
						Print_RHS();
					}


				});
				menu2.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						System.out.println(panel_state);
						sort_state = false;
						if(panel_state == 1){
							make_small_icon();
						}
						else if(panel_state == 2)
						{
							make_big_icon();
						}
						Print_RHS();
					}
				});
				popup.add(menu1);
				popup.add(menu2);
				popup.show((Component)e.getSource(), e.getX(), e.getY());
			}
		}
	}

	public void newDir(String s, int state){
		File dir = new File(getPath()+"/"+s);
		if(!dir.exists()){ 
			dir.mkdirs();
			this.viewFiles(getPath());
			this.panel_state = state;
			Print_RHS();
		}else{
			JOptionPane.showMessageDialog(null, "이미 존재하는 폴더명입니다");
		}
	}

	public void newFile(String s, int state){
		File newFile = new File(getPath()+"/"+s+".txt");

		if(!newFile.exists()){ 
			try{
				FileWriter fw = new FileWriter(newFile);
				this.viewFiles(getPath());
				this.panel_state = state;
				Print_RHS();
			}catch(Exception e){

			}
		}
	}

	public void del(String s){
		File newFile = new File(getPath() + s);
	}

	public void saveFile(String path){
		String temp2 = path.replace("\\", "/");
		try{
			//         File file = new File(temp2);
			//         OutputStream out = new FileOutputStream(file);
			//         String text = context.getText();
			//         byte[] buf = text.getBytes();
			//         out.write(buf);

			BufferedWriter fileout = new BufferedWriter(new FileWriter(temp2));
			context.write(fileout);
			JOptionPane.showMessageDialog(null, "정상적으로 저장하였습니다.");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "error 발생" + e.getMessage());
		}
	}



	public void setInfo(String s){
		this.info.setText(s);
	}

	public String getLast_path() {
		return last_path;
	}

	public void setLast_path(String last_path) {
		this.last_path = last_path;
	}

	public String getPath(){
		return ssRow;
	}

	public String getFileName(){
		return fileName;
	}

	public Vector getHistory(){
		return history_vector;
	}

	public void set_panel_state(int p) //WWW
	{
		this.panel_state = p;
	}
}