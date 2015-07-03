package peopleanalytics.logic;

//import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

//import org.w3c.dom.css.Rect;

import peopleanalytics.Main;

import org.opencv.core.Core;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.*; 
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
//import com.googlecode.javacv.cpp.opencv_objdetect.CascadeClassifier;



public class FaceDetection {
	public static String PATH_CASCADE_CLASSIFIER = "data/lbpcascade_frontalface.xml";
	
	public FaceDetection() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public BufferedImage run(File file) throws IOException {

		// Create a face detector from the cascade file in the resources
		// directory.
		CascadeClassifier faceDetector = new CascadeClassifier(new File(PATH_CASCADE_CLASSIFIER).getPath());
		Mat frame = Imgcodecs.imread(file.getPath());
                BufferedImage image = null;

		// Detect faces in the image.
		// MatOfRect is a special container class for Rect.
		MatOfRect faceDetections = new MatOfRect();
                Mat detectedFace = new Mat();
                Size size = new Size(125, 127);
		faceDetector.detectMultiScale(frame, faceDetections);

		Main.LOGGER.info(String.format("Detected: %s faces", faceDetections.toArray().length));
                Main.result = Main.result + " " + String.format("Detected: %s faces", faceDetections.toArray().length) + " ";
                Main.numberofpeople = faceDetections.toArray().length;

                for (Rect rect : faceDetections.toArray()) {
			        Imgproc.rectangle(
			        		frame, new Point(rect.x, rect.y), 
			        		new Point(rect.x + rect.width, rect.y + rect.height), 
			        		new Scalar(0, 255, 0));
			        detectedFace = new Mat(frame, rect);
			        Imgproc.resize(detectedFace, detectedFace, size);
			        Imgproc.cvtColor(detectedFace, detectedFace, Imgproc.COLOR_BGR2GRAY);
			        String gend = GenderRecognition.recognize(toBufferedImage(detectedFace));
                                Main.LOGGER.info(String.format("Gender: %s", gend));
                                if(Main.numberofpeople > 0) {
                                    Main.result =  Main.result + " " + "Gender: " + gend;
                                }
                                /*
			        Imgproc.putText(
			        		frame, gend, new Point(rect.x, rect.y-10), 
			        		Core.FONT_HERSHEY_PLAIN, 1.0, 
			        		new Scalar(0, 255, 0));
                                */
			     }
                
                image = toBufferedImage(detectedFace);

		return image;
                
                
		// Draw a bounding box around each face.
                /*
		for (Rect rect : faceDetections.toArray()) {
			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}

		MatOfByte byteMat = new MatOfByte();
		Imgcodecs.imencode(".jpg", frame, byteMat);
		byte[] bytes = byteMat.toArray();
		
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
                
		return img;
                */
	}
        
        private BufferedImage toBufferedImage(Mat matImage) 
							throws IOException {
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", matImage, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		return ImageIO.read(in);
	}


}
