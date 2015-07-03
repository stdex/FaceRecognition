package peopleanalytics.logic;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.opencv.core.Size;
import peopleanalytics.Main;
import peopleanalytics.tools.ImageInfo;

import peopleanalytics.ui.Window;

public class Listener implements ActionListener {
	private static Listener instance = new Listener();
	private static File fileRead;
        //private final Size size = new Size(125, 127); 

	private Listener() {

	}

	public static Listener getInstance() {
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {

		case "Open Image" :
			getAndShowImage();
			break;

		case "Detect Face" :
			FaceDetection faceDetection = new FaceDetection();
			try {
				BufferedImage img = faceDetection.run(fileRead);
                                //GenderRecognition genderOutput = new GenderRecognition();
                                //String gender = genderOutput.recognize(img);

				showImage(img, "1");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		}

	}



	private void getAndShowImage() {
		FileNameExtensionFilter imgFilter = 
				new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "bmp", "gif", "png");
		fileRead = getFile(imgFilter);

		if (fileRead != null) {
			Window.setDetectFaceMenuEnabled(true);
			showImage(fileRead);
		}
	}

	private File getFile(FileNameExtensionFilter imgFilter) {
		File defaultPath = new File("data");
		JFileChooser fc = new JFileChooser(defaultPath);
		fc.setFileFilter(imgFilter);

		if (fc.showOpenDialog(Window.getFrame()) == JFileChooser.APPROVE_OPTION) {
			fileRead = fc.getSelectedFile();
		}
                
                Main.result = "";
                Main.numberofpeople = 0;

		return fileRead;
	}

	private void showImage(BufferedImage img, String mode) throws IOException {
		JFrame frame = Window.getFrame();
		JPanel panel = Window.getPanel();
                
                Image resizedImg = null;

                if(mode.equals("2")) {
                    resizedImg = getResizedImage(img, 400);
                }
                else if(mode.equals("1")) {
                    resizedImg = img;
                }
                
		JLabel picLabel = new JLabel(new ImageIcon(resizedImg));

		panel.removeAll();
		panel.add(picLabel);
		panel.revalidate(); //  Force JPanel to repaint
                
                //JPanel panelResult = new JPanel(new BorderLayout());
                //JLabel resultLabel = new JLabel();
                //resultLabel.setText("123");
                //panelResult.add(resultLabel, BorderLayout.SOUTH);
                
                //panel.add(resultLabel, BorderLayout.SOUTH);

                picLabel.setText(Main.result);
                picLabel.setHorizontalTextPosition(JLabel.CENTER);
                picLabel.setVerticalTextPosition(JLabel.BOTTOM);
                picLabel.getParent().revalidate();
                
		frame.pack();  //  Resize JFrame to fit content
	}

	private void showImage(File file) {
		try {
			BufferedImage img = ImageIO.read(file);
                        
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(img, "jpg", os);
                        InputStream is = new ByteArrayInputStream(os.toByteArray());

                        ImageInfo ii = new ImageInfo();
                        ii.setInput(is); // in can be InputStream or RandomAccessFile
                        if (!ii.check()) {
                            System.err.println("Not a supported image file format.");
                            return;
                        }
                        
                        Main.result = Main.result + " " + "Quality: " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " +
                        ii.getBitsPerPixel() + " bits per pixel";
                        /*
                        System.out.println(ii.getFormatName() + ", " + ii.getMimeType() +
                        ", " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " +
                        ii.getBitsPerPixel() + " bits per pixel, " + ii.getNumberOfImages() +
                        " image(s), " + ii.getNumberOfComments() + " comment(s).");
                        */
                        
			showImage(img, "2");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Image getResizedImage(BufferedImage img, int newWidth) {
		double scale = (double) newWidth / img.getWidth();
		int newHeight = (int) (img.getHeight() * scale);

		return img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	}
}
