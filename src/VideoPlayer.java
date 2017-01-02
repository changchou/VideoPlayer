import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VideoPlayer extends JFrame {

	/**
	 * 
	 */
	private static final String NATIVE_LIBRARY_SEARCH_PATH = "D:\\Program Files (x86)\\VideoLAN\\VLC";
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	private JPanel panel;
	private JButton btnOpen;
	private JButton btnStop;
	private JButton btnScreen;
	private JPanel controlPanel;
	private JProgressBar progress;
	private JPanel menuPanel;
	private JMenu mnFile;
	private JMenuItem mntmOpenVideo;
	private JMenuItem mntmOpenSubs;
	private JMenuItem mntmExit;
	private JMenuBar menuBar;
	private JSlider slider;
	private JPanel progressPanel;
	private JLabel lblTime;
	private JLabel lblTotalTime;
	private int mx, my, jfx, jfy;
	private long totalTime;
	private static VideoPlayer frame;
	private JLabel lbName;
	private JButton btnOntop;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// Explicit Library Path
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new VideoPlayer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VideoPlayer() {

		// 自定义关闭按钮事件
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				mediaPlayer.release();
				System.exit(0);
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel videoPanel = new JPanel();
		contentPane.add(videoPanel, BorderLayout.CENTER);
		videoPanel.setLayout(new BorderLayout(0, 3));

		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		videoPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(5, 5));

		controlPanel = new JPanel();
		panel.add(controlPanel, BorderLayout.WEST);

		btnOpen = new JButton("Open");
		btnOpen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				int v = chooser.showOpenDialog(getParent());
				if (v == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();

					openVideo(file);
				}
			}
		});
		controlPanel.add(btnOpen);

		btnStop = new JButton("Stop");
		btnStop.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (btnStop.getText().toString().equals("Stop")) {
					mediaPlayer.stop();
				} else {
					mediaPlayer.play();
				}
			}
		});
		controlPanel.add(btnStop);
		btnStop.setVisible(false);

		btnScreen = new JButton("FullScreen");
		btnScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnScreen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setfullScreen();
			}
		});
		
		btnOntop = new JButton("OnTop");
		btnOntop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (frame.isAlwaysOnTop()) {
					frame.setAlwaysOnTop(false);
					btnOntop.setText("OnTop");
				}else {
					frame.setAlwaysOnTop(true);
					btnOntop.setText("OnTop √");
				}
			}
		});
		controlPanel.add(btnOntop);
		controlPanel.add(btnScreen);

		slider = new JSlider();
		slider.setValue(100);
		slider.setMaximum(200);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				mediaPlayer.setVolume(slider.getValue());
			}
		});
		controlPanel.add(slider);

		menuPanel = new JPanel();
		panel.add(menuPanel, BorderLayout.EAST);

		menuBar = new JMenuBar();
		menuBar.setAlignmentY(Component.CENTER_ALIGNMENT);
		menuPanel.add(menuBar);

		mnFile = new JMenu("File");
		mnFile.setIcon(
				new ImageIcon(VideoPlayer.class.getResource("/com/sun/java/swing/plaf/windows/icons/TreeOpen.gif")));
		menuBar.add(mnFile);

		mntmOpenVideo = new JMenuItem("Open Video ...");
		mntmOpenVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int v = chooser.showOpenDialog(null);
				if (v == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();

					openVideo(file);
				}
			}
		});
		mnFile.add(mntmOpenVideo);

		mntmOpenSubs = new JMenuItem("Open Subs ...");
		mntmOpenSubs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openSubs();
			}
		});
		mnFile.add(mntmOpenSubs);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnFile.add(mntmExit);

		progressPanel = new JPanel();
		panel.add(progressPanel, BorderLayout.NORTH);
		progressPanel.setLayout(new BorderLayout(10, 10));
		progressPanel.setVisible(false);

		progress = new JProgressBar();
		progressPanel.add(progress);
		progress.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				mediaPlayer.setTime((long) ((float) x / progress.getWidth() * mediaPlayer.getLength()));
			}
		});
		progress.setStringPainted(true);

		lblTime = new JLabel("00:00:00");
		lblTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTime.setHorizontalAlignment(SwingConstants.CENTER);
		progressPanel.add(lblTime, BorderLayout.WEST);

		lblTotalTime = new JLabel("00:00:00");
		lblTotalTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTotalTime.setHorizontalAlignment(SwingConstants.CENTER);
		progressPanel.add(lblTotalTime, BorderLayout.EAST);

		lbName = new JLabel("");
		panel.add(lbName, BorderLayout.CENTER);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);

		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
				// TODO Auto-generated method stub
				super.mediaStateChanged(mediaPlayer, newState);

				switch (newState) {
				case 3:
					// playing
					btnStop.setText("Stop");
					;
					totalTime = mediaPlayer.getLength();
					lblTotalTime.setText(String.format("%02d:%02d:%02d", totalTime / 1000 / 3600,
							(totalTime / 1000 / 60) % 60, (totalTime / 1000) % 60));
					break;
				case 4:
					// paused
					btnStop.setText("Play");
					;
					break;
				case 5:
					// stopped
					btnStop.setText("RePlay");
					lblTime.setText("00:00:00");
					progress.setValue(0);
					break;
				case 6:
					// finished
					mediaPlayer.stop();
					break;
				default:
					break;
				}
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				// TODO Auto-generated method stub
				super.timeChanged(mediaPlayer, newTime);

				lblTime.setText(String.format("%02d:%02d:%02d", newTime / 1000 / 3600, (newTime / 1000 / 60) % 60,
						(newTime / 1000) % 60));

				progress.setValue((int) ((float) newTime / totalTime * 100));
			}

		});

		mediaPlayer.setEnableKeyInputHandling(false);
		mediaPlayer.setEnableMouseInputHandling(false);

		Canvas videoSurface = mediaPlayerComponent.getVideoSurface();

		videoSurface.requestFocus();
		videoSurface.requestFocusInWindow();

		videoSurface.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);

				mx = e.getXOnScreen();
				my = e.getYOnScreen();
				jfx = getX();
				jfy = getY();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);

				mediaPlayer.pause();
			}

		});

		videoSurface.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				setLocation(jfx + (e.getXOnScreen() - mx), jfy + (e.getYOnScreen() - my));
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseMoved(e);

				if (mediaPlayer.isFullScreen()) {
					if (e.getY() > panel.getLocation().getY()) {
						panel.setVisible(true);
					} else {
						panel.setVisible(false);
					}
				}
			}

		});

		videoSurface.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					mediaPlayer.skip(-10000);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					mediaPlayer.skip(10000);
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					mediaPlayer.pause();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (mediaPlayer.isFullScreen()) {
						mediaPlayer.setFullScreen(false);
						panel.setVisible(true);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					setfullScreen();
				}
			}

		});

		// 文件拖拽
		new DropTarget(mediaPlayerComponent, new DropTargetAdapter() {

			@Override
			public void drop(DropTargetDropEvent dtde) {
				// TODO Auto-generated method stub
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					try {
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) dtde.getTransferable()
								.getTransferData(DataFlavor.javaFileListFlavor);
						for (File file : files) {
							openVideo(file);
						}
						dtde.dropComplete(true);
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void openVideo(File file) {
		lbName.setText(file.getName());
		String options[] = { "--subsdec-encoding=GB18030" };
		try {
			mediaPlayer.playMedia(URLDecoder.decode(new String(file.getAbsolutePath().getBytes("UTF-8")), "UTF-8"),
					options);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		progressPanel.setVisible(true);
		btnStop.setVisible(true);

	}

	public void openSubs() {
		JFileChooser chooser = new JFileChooser();
		int v = chooser.showOpenDialog(null);
		if (v == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			mediaPlayer.setSubTitleFile(file);
		}
	}

	public void exit() {
		mediaPlayer.release();
		mediaPlayerComponent.release();
		System.exit(0);
	}

	public void setfullScreen() {
		if (mediaPlayer.isFullScreen()) {
			mediaPlayer.setFullScreen(false);
			panel.setVisible(true);
		} else {
			mediaPlayer.setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(frame));
			mediaPlayer.toggleFullScreen();
			panel.setVisible(false);
		}

	}

}
