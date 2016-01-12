import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javafx.application.*;
import edu.dongguk.cse.pl.reportwritter.ISearchItem;//jar파일안에 있는 인터페이스 
/**
 * 기존 콘솔메뉴를 GUI로 구현한 클래스
 * @author 김현아
 *
 */
class MyFrame extends JFrame {
	public static String Open_PATH=null;//읽어올 파일 변수 
	public static String Print_PATH=null;//출력할 파일 변수 
	JFileChooser jfc=new JFileChooser();//입력파일 또는 출력 파일의 경로를 부를 때 파일을 선택할 수 있도록 팝업을 띄우기 위한 객체
	//각 버튼 초기화
	JButton append_Button=new JButton("붙이기");//삽입 버튼
	JButton print_Button=new JButton("출력");//출력버튼
	JButton Search_Button=new JButton("검색");//검색 버튼
	JButton replace_button=new JButton("변환");//변환버튼
	JButton Warning_page=new JButton("확인");//경고창의 버튼
	JFrame Warning=new JFrame("Warning!!!");//경고 메세지를 띄우기 위한 객체
	JPanel panel;//각 창의 내용을 저장하기 위한 객체
	JPanel Warning_panel=new JPanel();//경고창에 내용을 저장하기 위한 객체
	//메뉴바 구성을 위한 객체선언
	//CreateMenu()메소드에 객체 생성 및 초기화 함
	JMenuBar myMenu;
	JMenu mn_file;
	JMenu mn_edit;
	JMenuItem item_open;
	JMenuItem item_print;
	JMenuItem item_search;
	JMenuItem item_replace;
	JMenuItem item_append;
	//각 텍스트필드를 설명해줄 레이블 선언 또는 초기화
	JLabel first_label;
	JLabel second_label;
	JLabel open_file_label=new JLabel("입력파일명 :");
	JLabel print_file_label=new JLabel("출력파일명 :");
	static JLabel search_state=new JLabel("");//검색 상태를 알려줄 레이블
	static JLabel search_count=new JLabel("");//검색된 개수를 알려줄 레이블
	JLabel append_col;
	JLabel warning_message=new JLabel();//경고 메세지의 내용을 출력해줄 레이블
	//각 텍스트필드 초기화
	JTextField search=new JTextField(90);//검색단어 입력
	JTextField replace=new JTextField(90);//변환단어 입력
	JTextField open_file=new JTextField(60);//입력파일경로 입력
	JTextField print_file=new JTextField(60);//출력 파일경로 입력
	JTextField Line=new JTextField(20);//붙일 줄 입력
	JTextField Col=new JTextField(20);//붙일 열 입력
	JTextField append_text=new JTextField(60);//삽입할 텍스트 입력
	static DefaultListModel model;//리스트에 검색 결과들을 추가시키기 위한 객체
	JList list;//검색 결과를 저장할 리스트
	JScrollPane scr=new JScrollPane();//리스트에 스크롤을 붙이기 위한 객체
	/**
	 * MyFrame클래스의 생성자
	 */
	public MyFrame(){
		setLayout(null);
		CreateMenu();//메뉴바 구현
		CreateReplace();//변환 창 구현, 처음에는 변환 창으로 띄움
		setTitle("<객체지향언어와 실습>2014112076김현아");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setVisible(true);
		//경고창 설정
		Warning.setSize(300, 180);//경고창 사이즈 설정
		//경고창 내용 저장
		Warning_panel.add(warning_message);//메세지를 저장하는 레이블 추가
		Warning_page.addActionListener(new MyListener());
		Warning_panel.add(Warning_page);//버튼 추가
		Warning.add(Warning_panel);//경고창 프레임에 패널 저장
	}
	/**
	 * ActionListener 인터페이스를 구현한 MyListener클래스 
	 * 이벤트 처리함
	 * @author 김현아
	 *
	 */
	private class MyListener implements ActionListener{

		/**
		 * 각각의 이벤트 처리를 하는 메소드
		 * @param e
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			//메뉴바에서 "검색"을 눌렀을 경우 
			if(e.getSource()==item_search)
			{
				CreateSearch();//검색 화면을 띄움
			}
			//메뉴바에서 "변환"을 눌렀을 경우 
			else if(e.getSource()==item_replace)
			{
				CreateReplace();//변환기능을 수행할 화면을 띄움
			}
			//메뉴바에서 "열기"를 눌렀을 경우
			else if(e.getSource()==item_open)
			{
				//JFileChooser객체를 사용하여 파일 들을 나열해 놓은 팝업을 띄움
				if(jfc.showOpenDialog(item_open)==JFileChooser.APPROVE_OPTION)
				{
					open_file.setText(jfc.getSelectedFile().toString());//파일을 선택하면 open_file텍스트 필드에 경로 출력
					Open_PATH=jfc.getSelectedFile().toString();//Open_PATH에 해당 경로 저장
				}
			}
			//메뉴바에서 "출력"을 눌렀을 경우
			else if(e.getSource()==item_print)
			{
				///JFileChooser객체를 사용하여 파일 들을 나열해 놓은 팝업을 띄움
				if(jfc.showSaveDialog(item_print)==JFileChooser.APPROVE_OPTION)
				{
					print_file.setText(jfc.getSelectedFile().toString()+"."+jfc.getFileFilter().getDescription());//파일을 선택하면 print_file텍스트 필드에 경로 출력
					Print_PATH=print_file.getText();//Print_PATH에 해당 경로 저장
				}
			}
			//메뉴바에서 "붙이기"를 눌렀을 경우
			else if(e.getSource()==item_append)
			{
				CreateAppend();//"붙이기"메뉴의 화면 띄움
			}
			//"붙이기"버튼을 눌렀을 경우
			else if(e.getSource()==append_Button)
			{
				Open_PATH=open_file.getText();//만약 수동으로 파일명을 입력했을 경우를 대비하여 다시 한번 입력파일 텍스트 필드의 내용을 Open_PATH에 저장
				//Print_PATH=print_file.getText();//만약 수동으로 파일명을 입력했을 경우를 대비하여 다시 한번 출력파일 텍스트 필드의 내용을 Print_PATH에 저장
				//try-catch문을 이용하여 예외 처리함
				try{
					int a = Integer.parseInt(Line.getText());//Line.getText()에 입력받은 내용을 정수로 변환하여 a에 저장
					int b = Integer.parseInt(Col.getText());//Col.getText()에 입력받은 내용을 정수로 변환하여 b에 저장
					//삽입할 문자열을 입력하지 않았을 경우 NullPointerException발생시킴
					if(append_text.getText().equals(""))
					{
						throw new NullPointerException ();
					}
					Editor.Append(append_text.getText(), a,b);//Editor클래스에 있는 단어 삽입 메소드인 Append메소드호출 
					
				}
				//입출력 예외가 발생했을 경우
				//경고창 띄움
				catch(IOException e1)
				{
					warning_message.setText("알 수 없는 접근입니다!");
					Warning.setVisible(true);
				}
				//입력파일명 또는 단어를 작성하지 않았을 경우의 예외 처리
				//경고창 띄움
				catch (NullPointerException e2)
				{
					warning_message.setText("입력 경로 및 삽입단어를 확인해주세요!");
					Warning.setVisible(true);
				}
				//Line.getText()부분이나 Col.getText()부분이 입력되지 않았다면 정수로 변환하여 저장하는데 예외가 발생되므로 catch문장을 통해 예외처리를 함
				//경고메세지를 띄움
				catch(NumberFormatException a)
				{
					warning_message.setText("줄과 행을 잘 입력하세요!");
					Warning.setVisible(true);
				}
			}
			//출력 버튼을 눌렀을 경우 
			else if(e.getSource()==print_Button)
			{
				Print_PATH=print_file.getText();//만약 수동으로 파일명을 입력했을 경우를 대비하여 다시 한번 출력파일 텍스트 필드의 내용을 Print_PATH에 저장
				//try-catch구문을 이용하여 발생할 수 있는 예외 처리함
				try{
				    Editor.printWord(Print_PATH);//Editor클래스에 있는 printWord메소드를 호출하여 Print_PATH에 저장되어 있는 경로로 파일 출력
				}
				//입출력 예외가 발생했을 경우 경고창 띄움
				catch(IOException e1){
					warning_message.setText("알 수 없는 접근입니다!");//메세지 입력
					Warning.setVisible(true);//경고창 보임
				}
				//출력경로를 지정하지 않았을 경우 NullPointException이 발생하므로 이에 대한 처리
				//경고창 띄움
				catch (NullPointerException e2)
				{
					warning_message.setText("출력경로를 확인해주세요!");//메세지 입력
					Warning.setVisible(true);//경고창 보임
				}
			}
			//검색을 눌렀을 경우
			else if(e.getSource()==Search_Button)
			{
				Open_PATH=open_file.getText();//만약 수동으로 파일명을 입력했을 경우를 대비하여 다시 한번 입력파일 텍스트 필드의 내용을 Open_PATH에 저장
				//try-catch구문을 이용하여 발생할 수 있는 예외 처리함
				try{
					//검색단어 필드에 아무것도 써있지 않거나 입력할 파일의 경로가 null일 경우 NullPointerException발생시킴
					if(search.getText().equals("")||open_file.equals(""))
						throw new NullPointerException();
					//해당 경로의 파일을 지정하지 않은 경우 IOException발생 시킴
					if(Open_PATH==null)
					{
						throw new IOException();
					}
					//쓰레드 객체 생성함
					Thread t1=new Thread(new Editor(search.getText()));
					search_state.setText("검색 대기. . ");
					t1.start();//쓰레드 시작
				}
				//검색 단어 또는 입력파일명을 지정하지 않았다면 NullPointException이 발생하므로 이에 대한 처리
				//경고창 띄움
				catch (NullPointerException e2)
				{
					warning_message.setText("단어 입력 및 파일경로를 확인해주세요!");//경고창 메세지
					Warning.setVisible(true);//경고창 보임
				}
				//해당 경로의 파일을 찾을 수 없거나 지정되지 않은 경우 IOException발생 
				catch(IOException e3)
				{
					warning_message.setText("알 수 없는 접근입니다!");//경고창 메세지 입력
					Warning.setVisible(true);//경고창 보임
				}
			}
			//변환 버튼을 눌렀을 경우 
			else if(e.getSource()==replace_button)
			{
				Open_PATH=open_file.getText();//만약 수동으로 파일명을 입력했을 경우를 대비하여 다시 한번 입력파일 텍스트 필드의 내용을 Open_PATH에 저장
				Print_PATH=print_file.getText();//만약 수동으로 파일명을 입력했을 경우를 대비하여 다시 한번 출력파일 텍스트 필드의 내용을 Print_PATH에 저장
				//try-catch구문을 이용해 발생할 수 있는 예외에 대한 처리
				try {
					//검색단어 필드에 아무것도 써있지 않거나 입력할 파일의 경로가 null일 경우 NullPointerException발생시킴
					if(search.getText().equals("")||open_file.equals("")||Open_PATH==null)
						throw new NullPointerException();
					
					Editor.replaceWord(search.getText(), replace.getText());//Editor클래스의 변환 메소드인 replaceWord()를 호출하여 검색단어를 변환단어로 변환함
				} 
				//입출력 예외가 발생했을 경우 경고창 띄움(파일을 찾을 수 없는 경우 등)
				catch (IOException e1) {
					// TODO Auto-generated catch block
					warning_message.setText("알 수 없는 접근입니다!");//경고창 메세지 입력
					Warning.setVisible(true);//경고창 보임
				}
				//검색단어, 변환단어 또는 입력파일 경로를 지정하지 않았을 경우 NullPointerException이 발생할 수 있으므로 이에 대한 예외 처리
				//경고창 띄움
				catch (NullPointerException e2)
				{
					warning_message.setText("단어 입력 및 파일경로를 확인해주세요!");//경고창 메세지 
					Warning.setVisible(true);//경고창 보임
				}
			}
			//경고창 메세지의 "확인"버튼을 눌렀을 경우 경고창을 없애도록 설정함
			else if(e.getSource()==Warning_page)
			{
				Warning.setVisible(false);//경고창 없어짐
			}
		}
	}
	/**
	 * 기본적인 메뉴바를 구현한 메소드
	 */
	private void CreateMenu()
	{
		panel=new JPanel();//패널 객체 생성
		myMenu=new JMenuBar();//메뉴바 생성
		mn_file=new JMenu("파일");//"파일" 메뉴 
		mn_edit=new JMenu("편집");//"편집"메뉴 
		myMenu.add(mn_file);//파일메뉴 추가
		myMenu.add(mn_edit);//편집메뉴 추가
		item_open=new JMenuItem("열기");//"열기"하단메뉴
		item_print=new JMenuItem("출력");//"출력"하단메뉴
		item_search=new JMenuItem("검색");//"검색"하단메뉴
		item_replace=new JMenuItem("변환");//"변환"하단메뉴
		item_append=new JMenuItem("붙이기");//"붙이기"하단메뉴
		//각 메뉴에 맞는 하단 메뉴 추가
		mn_file.add(item_open);
		mn_file.add(item_print);
		mn_edit.add(item_search);
		mn_edit.add(item_replace);
		mn_edit.add(item_append);
		setJMenuBar(myMenu);//메뉴바 추가됨
		//각 메뉴에 이벤트 기능 추가
		item_open.addActionListener(new MyListener());//열기를 눌렀을 경우 이벤트기능 추가
		item_print.addActionListener(new MyListener());//출력을 눌렀을 경우 이벤트기능 추가
		item_search.addActionListener(new MyListener());//검색을 눌렀을 경우 이벤트기능 추가
		item_replace.addActionListener(new MyListener());//변환을 눌렀을 경우 이벤트기능 추가
		item_append.addActionListener(new MyListener());//붙이기를 눌렀을 경우 이벤트기능 추가
		jfc.setFileFilter(new FileNameExtensionFilter("txt","txt"));//열수 있는 파일을 텍스트 파일로 한정함
		jfc.setFileHidingEnabled(false);
		setVisible(true);//보이게 함
	}
	/**
	 * 메뉴바에서 "변환"을 눌렀을 경우 보이는 창을 구현한 메소드
	 */
	private void CreateReplace ()
	{
		panel.setLayout(null);
		panel.removeAll();//panel의 내용 모두 삭제
		panel.repaint();//다시 보이게 함
		first_label=new JLabel("검색할 단어 :");//first_label를 "검색할 단어"로 초기화
		second_label=new JLabel("변환할 단어 :");//second_label를 "변환할 단어"로 초기화
		search.setText("");//search텍스트 필드 초기화
		replace.setText("");
		open_file.setText("");//파일 열기 필드 초기화
		print_file.setText("");//파일 출력 필드 초기화
		search_count.setText("");//검색 결과 개수 레이블 초기화
		search_state.setText("");//검색 상태 레이블 초기화
		replace_button.addActionListener(new MyListener());//변환 버튼에 이벤트기능 추가
		print_Button.addActionListener(new MyListener());//출력 버튼에 이벤트기능 추가 
		model=new DefaultListModel();//검색 결과를 추가하기위한 객체
		list=new JList(model);//model에 추가되면 list에도 검색 결과가 추가됨
		scr=new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);//list에 스크롤 존재
		list.setVisibleRowCount(10);//최대 list를 10개 보이게 함
		//각 레이블 panel에 추가
		panel.add(scr);
		panel.add(first_label);
		panel.add(search);
		panel.add(second_label);
		panel.add(replace);
		panel.add(replace_button);
		panel.add(open_file_label);
		panel.add(open_file);
		panel.add(print_file_label);
		panel.add(print_file);
		panel.add(print_Button);
		panel.add(search_state);
		panel.add(search_count);
		//각 객체들 범위 지정
		print_file_label.setBounds(250,474,90,30);
		replace_button.setBounds(400,40,105,90);
		first_label.setBounds(10,50,90,30);
		second_label.setBounds(10,90,90,30);
		open_file_label.setBounds(10,142,90,30);
		search.setBounds(100,58,250,24);
		replace.setBounds(100,98,250,24);
		print_Button.setBounds(670,480,100,30);
		panel.setBounds(0, 0, 800, 600);
		scr.setBounds(150, 220, 500, 200);
		print_file.setBounds(350,480,300,24);
		open_file.setBounds(100,150,300,24);
		search_state.setBounds(100,430,100,30);
		search_count.setBounds(450,190,100,24);
		add(panel);//panel을 넣음
		setVisible(true);//보이게 함
	}
	/**
	 * 메뉴바에서 "붙이기"를 눌렀을 경우 보이는 창을 구현한 메소드
	 */
	private void CreateAppend()
	{
		panel.setLayout(null);
		panel.removeAll();//panel의 모든 내용 삭제
		panel.repaint();//다시 보이게함
		first_label=new JLabel("붙일 줄 :");//first_label를 "붙일 줄"로 초기화
		second_label=new JLabel("붙일 단어 :");//second_label를 "붙일 단어"로 초기화
		append_col=new JLabel("붙일 문자열");//append_col를 "붙일 열"로 초기화
		append_Button.addActionListener(new MyListener());//붙이기 버튼에 이벤트기능 추가
		print_Button.addActionListener(new MyListener());//출력 버튼에 이벤트기능 추가
		open_file.setText("");//파일 열기 필드 초기화
		print_file.setText("");//파일 출력 필드 초기화
		//객체들을 panel에 추가
		panel.add(first_label);
		panel.add(Line);
		panel.add(Col);
		panel.add(second_label);
		panel.add(append_col);
		panel.add(append_text);
		panel.add(append_Button);
		panel.add(print_Button);
		panel.add(open_file_label);
		panel.add(open_file);
		panel.add(print_file_label);
		panel.add(print_file);
		//각 객체들 배치
		append_Button.setBounds(450,40,105,120);
		print_Button.setBounds(670,480,100,30);
		first_label.setBounds(10,50,90,30);
		append_col.setBounds(10,78,90,30);
		second_label.setBounds(10,112,90,30);
		open_file_label.setBounds(10,142,90,30);
		print_file_label.setBounds(250,474,90,30);
		Line.setBounds(100,58,100,24);
		Col.setBounds(100,86,100,24);
		append_text.setBounds(100,120,250,24);
		open_file.setBounds(100,150,300,24);
		print_file.setBounds(350,480,300,24);
		panel.setBounds(0, 0, 800, 600);
		scr.setBounds(150, 220, 500, 200);
		add(panel);
	}
	/**
	 * 메뉴바에서 "검색"을 눌렀을 경우 보이는 창을 구현한 메소드
	 */
	private void CreateSearch()
	{
		panel.setLayout(null);
		panel.removeAll();//panel의 모든 내용 삭제
		panel.repaint();//다시 보이게 함
		search.setText("");//search텍스트 필드 초기화
		open_file.setText("");//파일 열기 필드 초기화
		print_file.setText("");//파일 출력 필드 초기화
		search_count.setText("");//검색 결과 개수 레이블 초기화
		search_state.setText("");//검색 상태 레이블 초기화
		first_label=new JLabel("검색할 단어 :");//"first_label을 "검색할 단어"로 초기화
		Search_Button.addActionListener(new MyListener()); //검색 버튼에 이벤트 추가
		print_Button.addActionListener(new MyListener());//출력 버튼에 이벤트 추가
		model=new DefaultListModel();//검색 결과를 추가하기위한 객체
		list=new JList(model);//model에 추가되면 list에도 검색 결과가 추가됨
		list.setVisibleRowCount(10);//최대 10개까지 보일 수 있게 함
		scr=new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);//list에 스크롤 존재
		//각 객체들을 panel에 추가
		panel.add(scr);
		panel.add(first_label);
		panel.add(search_state);
		panel.add(search);
		panel.add(open_file_label);
		panel.add(Search_Button);
		panel.add(print_file_label);
		panel.add(open_file);
		panel.add(print_file);
		panel.add(print_Button);
		panel.add(search_count);
		//각 객체들 배치함
		print_Button.setBounds(670,480,100,30);
		Search_Button.setBounds(400,40,105,90);
		first_label.setBounds(10,50,90,30);
		open_file_label.setBounds(10,142,90,30);
		print_file_label.setBounds(260,473,90,30);
		search_state.setBounds(100,430,100,30);
		open_file.setBounds(100,150,300,24);
		print_file.setBounds(350,480,300,24);
		search.setBounds(100,58,250,24);
		panel.setBounds(0, 0, 800, 600);
		scr.setBounds(150, 220, 500, 200);
		search_count.setBounds(450,190,100,24);
		add(panel);//panel을 추가
		setVisible(true);//보이게 함
	}
	
}
/**
 * 인터페이스 ISearchItem을 구현한 CSearchItem클래스 
 * @author 김현아
 *
 */
class CSearchItem implements ISearchItem{
	private int lineNo;//line을 관리하는 변수 (라인수)
	private int indexNo;//index을 관리하는 변수 (번째)
	//인터페이스에 대한 메소드 오버라이딩
	/**
	 * 멤버변수 indexNo를 반환
	 */
	@Override
	public int getIndexNo() {
		// TODO Auto-generated method stub
		return indexNo;
	}
	/**
	 * 멤버변수 lineNo를반환
	 */
	@Override
	public int getLineNo() {
		// TODO Auto-generated method stub
		return lineNo;
	}
	/**
	 * 객체의 멤버변수indexNo에 매개변수를 저장함
	 */
	@Override
	public void setIndexNo(int indexNo) {
		// TODO Auto-generated method stub
		this.indexNo=indexNo;	
	}
	/**
	 *객체의 멤버변수 lineNo에 매개변수 저장함 
	 */
	@Override
	public void setLineNo(int lineNo) {
		// TODO Auto-generated method stub
		this.lineNo=lineNo;
	}
}	
/**
 * main메소드를 포함하는 클래스
 * @author 김현아
 *
 */
public class JavaProject{
	/**
	 * MyFrame객체를 생성하여 MyFrame에서 구현한 기능을 사용할 수 있도록함
	 * @param args
	 */
	public static void main(String[] args)
	{
		MyFrame frame=new MyFrame();//MyFrame객체를 생성함
	}
}

/**
 * Runnable 인터페이스를 구현한 Editor클래스 (단어 검색을 쓰레드로 구현하기 위해)
 * 입력, 출력, 단어 검색(쓰레드로 구현), 단어 변환, 단어 삽입 메소드 포함되어 있음
 * @author 김현아
 *
 */
class Editor implements Runnable {
	static StringBuffer Printtxt=new StringBuffer("");
	String finding;//검색할 단어를 저장할 멤버변수
	/**
	 * Editor클래스의 생성자 
	 * @param find : 검색할 단어를 매개변수로 전달하여 멤버변수 finding에 저장함
	 */
	Editor(String find){
		finding=find;
	}
	/**
	 * 파일이 존재한다면 파일에 있는 내용 다음에 이어서 쓰던가 아니면 처음부터 입력받은 문자열을 출력하고, 만약 파일이 존재하지 않다면 해당 경로에 맞는 파일을 생성하고 문자열을 출력한다. 
	 * @param Open_PATH	:문자열을 출력해줄 파일의 경로
	 * @param write :파일에 출력할 문자열
	 * @throws IOException:모든 예외처리를 인정
	 */
	public static void printWord(String Print_PATH)throws IOException
	{
		File textFile =new File(Print_PATH);//File객체 textFile생성 
		FileReader input =new FileReader(MyFrame.Open_PATH);//Open_PATH에 지정되어 있는 파일을 읽어온다.
		BufferedReader br= new BufferedReader(input);//버퍼를 이용하여 in에 저장되어 있는 데이터를 읽는다. 
		int length=br.readLine().length();//맨 첫번째 줄만 읽어와서 length에 저장함.
		try{
			if(textFile.exists())//만약 파일이 존재한다면
			{
				//파일의 처음부터 다시 쓴다. 
					int j=0;
					BufferedWriter out = new BufferedWriter(new FileWriter(Print_PATH));
					//substring을 사용하므로 Printtxt의 길이-1만큼 for문을 돌려야한다.
					for(int i=0;i<Printtxt.length()-1;i++)
					{
						String str=Printtxt.substring(i,i+1);//문자하나씩 str에 저장함
						//줄을 맞추기위해서 j+1가 length로 나눈 나머지가 0이고, 0이아니라면 해당 문자를 써주고 한줄 띄어준다.
						//단,  Append메소드에서 ";"를 구분자로 하여 행을 띄어줄 것이었으므로 ";"일 경우 out.newLine()을 실행하여 한 줄을 띄우고, j를 -1로 저장하여 그 다음줄은 length의 길이만큼 잘릴 수 있게 함
						if(str.equals(";")||str.equals("."))
							{
							out.newLine();
							j=0;
							}
						else if((j+1)%length==0&&j!=0)
						{
							out.write(str);
							out.newLine();
						}
						//아니라면 해당 문자만 써준다.
						else out.write(str);
						j++;
					}
					
					out.flush();
					out.close();//파일 닫음
			}
			//만약 파일이 존재 하지 않는다면
			else {
				 //FileWriter를 사용하여 해당경로에 파일 생성
				 FileWriter fw = new FileWriter(textFile, true) ;
				 //BufferedWriter를 사용하여 파일에 데이터 출력할 수 있게 한다.
				 BufferedWriter out = new BufferedWriter(new FileWriter(textFile));
				//substring을 사용하므로 Printtxt의 길이-1만큼 for문을 돌려야한다.
				 int j=0;
				 //Printtxt에 저장되어 있는 문자들을 하나씩 받아오고, j를 하나씩 증가시켜 j가 length로 나누어 떨어지면 한줄을 띄어쓰게 함으로써 length만큼씩 문자열을 잘라 출력할 수 있게한다.
				 for(int i=0;i<Printtxt.length()-1;i++)
					{
					 String str=Printtxt.substring(i,i+1);//문자하나씩 str에 저장함
					 	//줄을 맞추기위해서 j+1가 length로 나눈 나머지가 0이고, 0이아니라면 해당 문자를 써주고 한줄 띄어준다.
						//단,  Append메소드에서 ";"를 구분자로 하여 행을 띄어줄 것이었으므로 ";"일 경우 out.newLine()을 실행하여 한 줄을 띄우고, j를 -1로 저장하여 그 다음줄은 length만큼 잘릴 수 있게 함
						if(str.equals(";")||str.equals("."))
							{
							out.newLine();
							j=0;
							}
						//j이 length로 나누어떨어지고 0이 아닐 때 해당 문자는 작성하고 한줄을 띄운다.
						else if((j+1)%length==0&&j!=0)
						{
							out.write(str);
							out.newLine();
						}
						//아니라면 해당 문자만 써준다.
						else out.write(str);	
						j++;
					}
					out.flush();
					out.close();//파일 닫음
			}
		}
		//만약 예외가 생겼다면 에러가 났음을 알려줌
		catch(IOException e){
			System.out.println("에러가 났습니다. ");}
	}

	/**
	 *  매개변수로 전달받은 line과 col의 위치에 str을 삽입하는 메소드
	 * @param str : 삽입할 단어
	 * @param line : 단어를 삽입할 줄
	 * @param col : 단어를 삽입할 열
	 * @throws IOException
	 **/
	public static void Append(String str, int line,int col) throws IOException
	{
		int r=0;
		Printtxt=new StringBuffer("");
		String bline;	//텍스트를 한줄씩 저장할 string
		StringBuffer appendWord=new StringBuffer("");//appendWord스트링 버퍼 생성및 초기화
		File file=new File(MyFrame.Open_PATH);//MyFrame.Open_PATH에 저장되어 있는 경로로 파일 불러옴
		FileReader in =new FileReader(file);//MyFrame.Open_PATH에 지정되어 있는 파일을 읽어온다.
		BufferedReader br= new BufferedReader(in);//버퍼를 이용하여 in에 저장되어 있는 데이터를 읽는다. 
		
		//파일 끝까지 읽을 때까지 while문을 돌림
		while ((bline=br.readLine())!=null) {
			r++;//줄을 한 줄씩 더함
			//현재의 줄이 line일 경우 
			if(r==line)
				{
					//해당 라인과 열까지의 문자열을 appendWord에 붙이고, 그 다음 str을 붙이고 ";"를 붙인 후 다시 bline의 나머지를 붙인다.
				   //";"를 붙이는 이유는 파일에 출력할 때 ";"을 만났을 경우 새로운 줄로 바꿔주기 위한 구분자 역할을 하게 하기 위함이다.
					appendWord.append(bline.substring(0,col-1)+str+";"+bline.substring(col-1));
				}
			//해당 라인이 아니라면 bline을 appendWord에 붙임
			else appendWord.append(bline);//만약 r이 line이 아니라면 bline과 개행문자 더한 것을 appendWord에 더한다.
		}
		Printtxt=appendWord;//printWord()메소드에서 출력하기 위해 static변수 Printtxt에 저장함
	}
	/**
	 * 검색단어를 변환단어로 바꿔주는 메소드
	 * @param finding : 검색할 단어
	 * @param replace : 변환할 단어
	 * @throws IOException
	 **/
	public static void replaceWord(String finding, String replace)throws IOException
	{
		FileReader input =new FileReader(MyFrame.Open_PATH);//MyFrame.Open_PATH에 지정되어 있는 파일을 읽어온다.
		FileReader input2 =new FileReader(MyFrame.Open_PATH);//MyFrame.Open_PATH에 지정되어 있는 파일을 읽어온다.
		ArrayList<ISearchItem> wordSearch=new ArrayList();//단어 검색 결과를 저장할 ArrayList<ISearchItem>형 객체 생성
		//두번 해당 경로의 파일의 데이터를 읽어 와야하므로 BufferedReader 객체를 두번 생성한다.
		BufferedReader br= new BufferedReader(input);
		BufferedReader br2= new BufferedReader(input2);
		int i=1,j=0,k=0,p=0;//k,p는 줄수를 가리키는 변수, j는 열을 가리키는 변수
		String find="";//finding과 텍스트 문자를 비교하기 위해 사용될 String변수
		String bline;	//텍스트를 한줄씩 저장할 string
		int sum=0;//여러줄에 걸쳐있을 때 검색 문자수와 읽어온 줄의 문자수를 비교할 때 사용하는 변수
		int row_s=0;//row_s초기화
		//단어가 여러번 나올 것을 대비하여 줄과 열을 저장할 배열을 선언함
		int row_a[]=new int [30];
		int col_a[]=new int [30];
		//배열 초기화
		for(int n=0;i<30;i++)
		{
				row_a[n]=0;
				col_a[n]=0;
		}
		String bl,blank=" ",str=" ";//공백 지정
		//파일의 데이터를 모두 읽을 때까지 while문을 돌림
		while ((bline=br.readLine())!=null) {
			k++;//1줄을 더해줌
			//각 한줄씩의 길이만큼 for문을 돌림
			for(i=0;i<bline.length();i++)
			{
				find=finding.substring(0,1);	//찾고자 하는 문자의 첫번째 문자를 str1에 저장한다.
				//만약 j가 bline.length-1보다 크다면 (즉, 그 줄의 끝이라면) for문을 나가서 다음 줄을 받아온다.
				if(j>bline.length()-1)
					 break;
				bl=bline.substring(j,j+1);	//1줄씩 받아온 문자열을 한 문자씩 잘라서 bl에 저장
				//만약 j가 0이라면, 즉 문장의 첫 시작이라면, blank에 " "을 저장한다.
				if(j==0)
					blank=" ";
				//만약 bl의 위치가 문장의 첫 시작이 아니라면 bl의 바로 앞 문자를 blank에 저장한다.
				else if(j!=0)
					blank=bline.substring(j-1,j);
				String bk=" ";	//blank와 비교하기 위한 string형 변수
				sum=bline.length()-j;	//만약 여러줄에 걸쳐있을 것을 대비하여 sum에 bline.length()-j(j의 위치에서부터 bline의 끝까지 사이의 문자 수)를 저장한다.
				//만약 bl과 find가 같고, blank가 bk와 같다면 계속해서 문자열 비교
				if((find.equals(bl))&&(bk.equals(blank)))
				{
					j++;	//다음 텍스트의 문자의 위치로 이동
					str=bline;//str에 bline저장
					find=finding.substring(1);//finding의 두번째 문자부터 끝까지 find에 저장
					//만약 find에 남아있는 문자의 수가 str에 남아있는 문자수 보다 크다면 다음 줄을 받아오는 작업을str에 있는 문자수가 더 클때까지 계속해서 실행한다. 
					if((j+finding.length()-1)>=str.length())
					{
					 //sum에 다음 줄을 계속해서 받아올동안 한 줄에 속해있는 문자수(128)를 더한다. 
					 //만약 sum이 finding.length()보다 작다면 계속해서 다음줄을 받아와 str에 더해준다. 
						p=k;//p에 문자열이 처음으로 나오는 부분의 줄을 저장한다.
						while(sum<finding.length())
						{
							bline=br.readLine();//텍스트의 한줄씩 받아온다.
							sum+=128;	//한줄에 속해있는 문자수를 더해준다.
							str+=bline;	//계속해서 한줄씩 받아와 str에 더한다.
							k++;//줄을 계속해서 받아왔으므로 더해준다.	
						}
						//만약 find에 남아있는 문자의 수가 str에 남아있는 문자수 보다 크다면 for문을 나간다.
						if((j+finding.length()-1)>=str.length())
						   break;
						bl=str.substring(j,j+finding.length()-1);	//bl에 str의 j번째 부터 j+finding.length()-1전까지의 문자열을 저장한다.
						//만약 find와 bl이 같다면 줄과 열을 출력한다.
						if(find.equals(bl))
						{
							//str의 j+finding.length()-1번째 문자가 (문자열을 다 비교한 뒤 그 끝이 띄어쓰기로 이루어져있는지 확인, 포함되어 있는 단어는 출력하지 않기 위함)가 " "라면 줄과 열을 출력
							String str4=str.substring(j+finding.length()-1,j+finding.length());	
							if(str4.equals(bk))
								{
								//CSearchItem클래스의 객체 cs생성
								CSearchItem cs=new CSearchItem();
								//cs객체의 멤버함수 setLineNo를 호출하여 p를 멤버변수 LineNo에 저장함
								cs.setLineNo(p);
								//cs객체의 멤버함수 setIndexNo를 호출하여 j를 멤버변수 IndexNo에 저장함
								cs.setIndexNo(j);
								//count인덱스에 cs를 요소로 저장함
								wordSearch.add(cs);
								//replace메소드애 매개변수로 넣을 row_a[row_s], col_a[row_s]에 단어의 위치 저장함
								row_a[row_s]=p;
								col_a[row_s]=j;
								row_s++;//다음 배열의 원소로이동
								//MyFrame.print_result.append(p+"번째 줄"+j+"번째\r\n");	//write에 줄과 열을 나타내는 문자열 저장함	
								}
						}
							break; 
					}
					//여러줄에 걸쳐져 있지 않은 경우 
					bl=bline.substring(j,j+finding.length()-1);//bl에 str의 j번째 부터 j+finding.length()-1전까지의 문자열을 저장한다.
					//만약 find와 bl이 같고 텍스트에서 비교한 단어 후 바로 띄어쓰기가 왔을 경우 줄과 열을 출력한다.
				 		if(find.equals(bl))
				 		{
				 			//bline의 j+finding.length()-1번째 문자가 (문자열을 다 비교한 뒤 그 끝이 띄어쓰기로 이루어져있는지 확인, 포함되어 있는 단어는 출력하지 않기 위함)가 " "라면 줄과 열을 출력
				 			String str2=bline.substring(j+finding.length()-1,j+finding.length());	
				 			if(str2.equals(bk))
				 				{
				 				//CSearchItem클래스의 객체 cs생성
								CSearchItem cs=new CSearchItem();
								//cs객체의 멤버함수 setLineNo를 호출하여 p를 멤버변수 LineNo에 저장함
								cs.setLineNo(k);
								//cs객체의 멤버함수 setIndexNo를 호출하여 j를 멤버변수 IndexNo에 저장함
								cs.setIndexNo(j);
								//count인덱스에 cs를 요소로 저장함
								wordSearch.add(cs);
				 				//replace메소드애 매개변수로 넣을 row_a[row_s], col_a[row_s]에 단어의 위치 저장함
				 				row_a[row_s]=k;
								col_a[row_s]=j;
								row_s++;//다음 배열의 원소로 이동
								//MyFrame.print_result.append(k+"번째 줄"+j+"번째\r\n");	//write에 줄과 열을 나타내는 문자열 저장함	
				 				}
				 		}
				}		
					j=i+1;		//j를 i+1로 저장하여 다음 문자를 비교할 수 있도록 한다.
			}
				j=0;	//j를 초기화해서 다음 줄의 처음부분부터 비교할 수 있게 한다.
	    }
		//for문을 돌려 DefaultListModel 변수 model에 ArrayList<ISearchItem>형 변수 wordSearch에 저장되어 있는 내용을 저장함
		for(int h=0;h<row_s;h++)
			MyFrame.model.add(h,wordSearch.get(h).getLineNo()+"줄의 "+wordSearch.get(h).getIndexNo()+"번째에 있습니다.");
		
		String replace_w=" ";
		int r=0;//현재 줄수를 알려주는 변수
		bl="";str="";//공백 지정
		String line="";//검색단어가 포함되어 있는 줄을 모두 더함(3줄에 걸쳐있다면 3줄을 더해서 line에 저장함)
		int count=0;//row_a와 col_a의 배열의 원소를 순차적으로 접근하기 위한 변수
		StringBuffer Replacetext=new StringBuffer("");//Replacetext초기화
		Printtxt=new StringBuffer("");//static변수 Printtxt 초기화
		
		//파일의 데이터를 다 읽어올 때까지 while문 돌림
		while ((bline=br2.readLine())!=null) {
			r++;//줄을 한 줄씩 더함
			//만약 읽어온 줄이 찾은 단어의 위치의 줄일 경우 
			if(r==row_a[count])
			{				
				//개행문자를 포함하고 있어 줄을 추가적으로 받아야하는 경우 
				if((bline.length()-col_a[count]+1)<finding.length())
				{
					//bl에 찾고자하는 단어에서 col_s를 빼고 1을 더한 값 이후의 문자들을 저장함.(그 줄에서 단어가 모두 포함되어 있지 않고 다음 줄까지 이어지므로 그 줄에서 포함되는 부분을 제외하고 그 이후의 문자열을 저장하는 것)	
					bl=finding.substring(bline.length()-col_a[count]+1);			
					line+=bline;//읽어온 줄을 더함
					//계속해서 줄을 읽어와야하는 경우 
					while(true)
					{
						bline=br2.readLine();//한줄을 더 읽어옴
						//지금까지 읽어온 줄에 검색단어가 포함되어 있을 경우 
						col_a[count]=0;//새로운 줄을 읽어왔으므로 col_s는 새로운 줄의 첫번째를 가리켜야한다.
						if(bline.length()>bl.length())
						{
							line+=bline;//line에 bline을 더함
							r++;//읽어온 줄의 수 더함
							break;//while문 나감
						}
						//계속해서 줄을 읽어와야하는 경우 
						else 
						{
							//bl에서 bline.length()번째부터의 문자열을 bl에 저장함 
							bl=bl.substring(bline.length());
							line+=bline;//line에 bline을 더함
							r++;//읽어온 줄의 수 더함
						}	
					}					
					replace_w=line.replaceAll(finding, replace);	//검색단어를 교체 단어로 바꿈			
					Replacetext.append(replace_w); //Replacetext에 바꾼 줄을 더함
				}
				
				else{
				replace_w=bline.replaceAll(finding, replace);//검색던어를 교체단어로 바꿈
				Replacetext.append(replace_w);//Replacetext에 바꾼 줄을 더함
				}
				count++;//다음 row_a와 col_a의 원소로 접근함
			}
			else 
			Replacetext.append(bline);		//검색단어의 위치의 줄이 아니라면 불러온 줄을 Replacetext에 더함	
		}
		 Printtxt.append(Replacetext);//Replacetext를 static변수 Printtxt에 저장함.(파일 출력을 위함)
	}
	
	/**
	 * 단어 검색을 위한 run()메소드 오버라이딩
	 * 단어 검색 기능을 쓰레드로 만듦으로써 단어 검색 도중 사용자 입력이 가능하도록 하였다.
	 * 단어의 검색 결과는 ArrayList<ISearchItem>형변수에 저장하고, run메소드의 끝부분에 리스트에 추가하도록 함
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		    MyFrame.search_state.setText("검색 중 . . . ");
		    Printtxt=new StringBuffer("");
			int i=1,j=0,k=0,p=0;//k는 줄수를 가리키는 변수
			String find="";//finding과 텍스트 문자를 비교하기 위해 사용될 String변수
			String bline;	//텍스트를 한줄씩 저장할 string
			ArrayList<ISearchItem> wordSearch=new ArrayList();//단어 검색 결과를 저장할 ArrayList<ISearchItem>형 객체 생성
			int sum=0;//여러줄에 걸쳐서 있는 단어일 경우 검색단어와 읽어온 줄의 단어의 개수를 비교하기 위한 변수
			//try-catch문으로 구성됨
			try {
				File file=new File(MyFrame.Open_PATH);
				FileReader in;
				in = new FileReader(file);
				BufferedReader br= new BufferedReader(in);//버퍼를 이용하여 in에 저장되어 있는 데이터를 읽는다. 
			//TEXT_Open_PATH에 지정되어 있는 파일을 읽어온다.
				
			String bl,blank=" ",str=" ";//공백 지정
			int count=0;
			while ((bline=br.readLine())!=null) {
				k++;//1줄을 더해줌
				//각 한줄씩의 길이만큼 for문을 돌림
				for(i=0;i<bline.length();i++)
				{
					find=finding.substring(0,1);	//찾고자 하는 문자의 첫번째 문자를 str1에 저장한다.
					//만약 j가 bline.length-1보다 크다면 (즉, 그 줄의 끝이라면) for문을 나가서 다음 줄을 받아온다.
					if(j>bline.length()-1)
						 break;
					bl=bline.substring(j,j+1);	//1줄씩 받아온 문자열을 한 문자씩 잘라서 bl에 저장
					//만약 j가 0이라면, 즉 문장의 첫 시작이라면, blank에 " "을 저장한다.
					if(j==0)
						blank=" ";
					//만약 bl의 위치가 문장의 첫 시작이 아니라면 bl의 바로 앞 문자를 blank에 저장한다.
					else if(j!=0)
						blank=bline.substring(j-1,j);
					String bk=" ";	//blank와 비교하기 위한 string형 변수
					sum=bline.length()-j;	//만약 여러줄에 걸쳐있을 것을 대비하여 sum에 bline.length()-j(j의 위치에서부터 bline의 끝까지 사이의 문자 수)를 저장한다.
					//만약 bl과 find가 같고, blank가 bk와 같다면 계속해서 문자열 비교
					if((find.equals(bl))&&(bk.equals(blank)))
					{
						j++;	//다음 텍스트의 문자의 위치로 이동
						str=bline;//str에 bline저장
						find=finding.substring(1);//finding의 두번째 문자부터 끝까지 find에 저장
						//만약 find에 남아있는 문자의 수가 str에 남아있는 문자수 보다 크다면 다음 줄을 받아오는 작업을str에 있는 문자수가 더 클때까지 계속해서 실행한다. 
						if((j+finding.length()-1)>=str.length())
						{
						 //sum에 다음 줄을 계속해서 받아올동안 한 줄에 속해있는 문자수(128)를 더한다. 
						 //만약 sum이 finding.length()보다 작다면 계속해서 다음줄을 받아와 str에 더해준다. 
							p=k;//p에 문자열이 처음으로 나오는 부분의 줄을 저장한다.
							while(sum<finding.length())
							{
								bline=br.readLine();//텍스트의 한줄씩 받아온다.
								sum+=128;	//한줄에 속해있는 문자수를 더해준다.
								str+=bline;	//계속해서 한줄씩 받아와 str에 더한다.
								k++;//줄을 계속해서 받아왔으므로 더해준다.	
							}
							//만약 find에 남아있는 문자의 수가 str에 남아있는 문자수 보다 크다면 for문을 나간다.
							if((j+finding.length()-1)>=str.length())
							   break;
							bl=str.substring(j,j+finding.length()-1);	//bl에 str의 j번째 부터 j+finding.length()-1전까지의 문자열을 저장한다.
							//만약 find와 bl이 같다면 줄과 열을 출력한다.
							if(find.equals(bl))
							{
								//str의 j+finding.length()-1번째 문자가 (문자열을 다 비교한 뒤 그 끝이 띄어쓰기로 이루어져있는지 확인, 포함되어 있는 단어는 출력하지 않기 위함)가 " "라면 줄과 열을 출력
								String str4=str.substring(j+finding.length()-1,j+finding.length());	
								if(str4.equals(bk))
									{
									//CSearchItem클래스의 객체 cs생성
									CSearchItem cs=new CSearchItem();
									//cs객체의 멤버함수 setLineNo를 호출하여 p를 멤버변수 LineNo에 저장함
									cs.setLineNo(p);
									//cs객체의 멤버함수 setIndexNo를 호출하여 j를 멤버변수 IndexNo에 저장함
									cs.setIndexNo(j);
									//count인덱스에 cs를 요소로 저장함
									wordSearch.add(cs);
								     //count을 1증가시킴
									count++;
									}						
							}
								 break;
						}
						//여러줄에 걸쳐져 있지 않은 경우 
						bl=bline.substring(j,j+finding.length()-1);//bl에 str의 j번째 부터 j+finding.length()-1전까지의 문자열을 저장한다.
					 //만약 find와 bl이 같고 텍스트에서 비교한 단어 후 바로 띄어쓰기가 왔을 경우 줄과 열을 출력한다.
					 		if(find.equals(bl))
					 		{
					 			//bline의 j+finding.length()-1번째 문자가 (문자열을 다 비교한 뒤 그 끝이 띄어쓰기로 이루어져있는지 확인, 포함되어 있는 단어는 출력하지 않기 위함)가 " "라면 줄과 열을 출력
					 			String str2=bline.substring(j+finding.length()-1,j+finding.length());	
					 			if(str2.equals(bk))
					 			{
					 				//CSearchItem클래스의 객체 cs생성
									CSearchItem cs=new CSearchItem();
									//cs객체의 멤버함수 setLineNo를 호출하여 p를 멤버변수 LineNo에 저장함
									cs.setLineNo(k);
									//cs객체의 멤버함수 setIndexNo를 호출하여 j를 멤버변수 IndexNo에 저장함
									cs.setIndexNo(j);
									//count인덱스에 cs를 요소로 저장함
									wordSearch.add(cs);
								     //count을 1증가시킴
								     count++;
								  }
					 		}
					}
						j=i+1;		//j를 i+1로 저장하여 다음 문자를 비교할 수 있도록 한다.
				}
					j=0;	//j를 초기화해서 다음 줄의 처음부분부터 비교할 수 있게 한다.			
		    }
			//for문을 돌려 DefaultListModel 변수 model에 ArrayList<ISearchItem>형 변수 wordSearch에 저장되어 있는 내용을 저장함
			for(int h=0;h<count;h++)
				MyFrame.model.add(h,wordSearch.get(h).getLineNo()+"줄의 "+wordSearch.get(h).getIndexNo()+"번째에 있습니다.");
			
			for(int h=0;h<count;h++)
				Printtxt.append(MyFrame.model.getElementAt(h));
			
			MyFrame.search_count.setText("검색 결과 : "+count+"개");//검색결과가 몇개인지 search_count레이블에 출력함
		}
	 catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			MyFrame.search_state.setText("검색 종료");//
	}
		
}

