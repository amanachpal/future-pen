package com.futurecam.here;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class ColoredObjectTrack implements Runnable {
	int[][] rect=new int[100][4];
	int rectRowCtr=0;
	int[][] black;//1st row x, 2nd row y
    final int INTERVAL = 1000;// 1sec
    final int CAMERA_NUM =0; // Default camera for this time
    boolean firstTime=true;
    boolean marking = false;
    boolean canvasFirstClicked=false;
    final String INPUT_LOC="C:\\wamp\\www\\uploads\\image.png";
    final String OUTPUT_LOC="C:\\wamp\\www\\uploads\\output.txt";
    final String TESSERACT_LOC="C:\\Users\\USER\\Pictures\\Tessereact\\Tesseract-OCR";
    final String CONFIG_FILE="alphanum";
    //final String LANG_LOC = TESSERACT_LOC +""
    final String OUTPUT_FORMAT="png";
    final String TESSERACT_COMMAND = "C:/Program Files/Tesseract-OCR/tesseract "+INPUT_LOC+" "+
    OUTPUT_LOC+"-l eng "+CONFIG_FILE;
    BufferedImage imgGenerator;
    Graphics igGraphics;
    /**
     * Correct the color range- it depends upon the object, camera quality,
     * environment.
     */
   //static CvScalar rgba_min = cvScalar(0, 74, 0, 0);// RED wide dabur birko
  // static CvScalar rgba_max = cvScalar(119, 255, 115, 0);
   // static CvScalar rgba_min=cvScalar(250,250,250,0);
   // static CvScalar rgba_max=cvScalar(250,250,250,0);
    static CvScalar rgba_min = cvScalar(0,0,0,0);
    static CvScalar rgba_max = cvScalar(0,0,0,0);
    
   // static CvScalar rgba_min = cvScalar(250,250,250,0);
    //static CvScalar rgba_max = cvScalar(250,250,250,0);
    
    int ctr; //counts number of frames with no log
    final int climit = 3;//if ctr exceeds climit, then new letter is started
    int pposX,pposY;
    IplImage image;
    CanvasFrame canvas = new CanvasFrame("Web Cam Live");
    CanvasFrame canvas2 = new CanvasFrame("Web Cam Stream 2");
    CanvasFrame path = new CanvasFrame("Detection");
    int ii = 0;
    CanvasFrame imgCanvas = new CanvasFrame("Image");
    JPanel canvasHolder = new JPanel();
    JPanel jp = new JPanel();
    JButton start = new JButton("Start");
    JButton convert = new JButton("Check");
    JButton mark = new JButton("Mark");
    public ColoredObjectTrack() {
    	imgCanvas.setContentPane(canvasHolder);
    	canvasHolder.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(marking)
				{
					int x = arg0.getX();
					int y = arg0.getY();

					canvasFirstClicked=!canvasFirstClicked;

					System.out.println("CLICKED "+canvasFirstClicked+"X:"+x+"Y:"+y);
					if(canvasFirstClicked)
					{
						rectRowCtr++;
						rect[rectRowCtr-1][0]=x;
						rect[rectRowCtr-1][1]=y;
					}
					else
					{
						rect[rectRowCtr-1][2]=x;
					    rect[rectRowCtr-1][3]=y;
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
    	});
        //canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        path.setContentPane(jp);
        mark.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0){
				marking=!marking;
				if(marking)//if starting new marking
				{
					mark.setText("Set");
					rectRowCtr=0;
					canvasFirstClicked=false;
				}
				else
				{
					mark.setText("Mark");
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
        	
        });
        start.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				firstTime=true;
				Graphics g = jp.getGraphics();
				g.clearRect(0, 0, jp.getWidth(), jp.getHeight());
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
        	
        });
        convert.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				generateOCR(imgGenerator);
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
        	
        });
        jp.add(start);
        jp.add(convert);
        jp.add(mark);
        
        canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);       
    }

	private void generateOCR(BufferedImage bi) {
		try{
			File f = new File(INPUT_LOC);
			writeImage(bi,OUTPUT_FORMAT,f);
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(TESSERACT_LOC));
			//pb.command("tesseract "+"untitled.png"+" "+"output"+" -l eng " + CONFIG_FILE);
			pb.command("tesseract",INPUT_LOC,OUTPUT_LOC.replace(".txt", ""),CONFIG_FILE);
			Process p=pb.start();
			p.waitFor();
			System.out.println(new Scanner(new File(OUTPUT_LOC)).nextLine());
		}
		catch(Exception e)
		{
			System.out.println("Writing "+e.getMessage());
		}
	}
    private void writeImage(BufferedImage bi, String formatName, File output)throws IOException//returns output location
    {
    	ImageIO.write(bi, formatName, output);
    }
    @Override
    public void run() {
    	
        FrameGrabber grabber = new OpenCVFrameGrabber(CAMERA_NUM);
        try {
            grabber.start();
            IplImage img;
            int posX = 0;
            int posY = 0;
            while (true) {
                img = grabber.grab();
                canvasHolder.getGraphics().drawImage(img.getBufferedImage(), 20, 20, null);
                /*if(marking)
                {
                	//System.out.println(rectRowCtr);
                	canvas2.showImage(IplImage.createFrom(blacken(img.getBufferedImage(),false)));
                }*/
                if (img != null) {
                	if(firstTime)
                	{
                		imgGenerator = new BufferedImage(img.width(),img.height(),BufferedImage.TYPE_INT_RGB);
                		findBlack(getThresholdImage(img).getBufferedImage());
                		//detectThrs.release();
                		firstTime=false;
                	}
                	//System.out.println("NOT TRUE");
                    // show image on window
                    //cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise
                    img=IplImage.createFrom(blacken(img.getBufferedImage(),true));
                    canvas2.showImage(img);//MY LINE
                    //jp.getGraphics().drawImage(i, 0, 0, i.getWidth(),i.getHeight(),0,0,i.getWidth(),i.getHeight(),null);
                    IplImage detectThrs = getThresholdImage(img);
                    
                    canvas.showImage(detectThrs);//MY LINE
                    
                    CvMoments moments = new CvMoments();
                    cvMoments(detectThrs, moments, 1);
                    //cvReleaseImage(img);
                    cvReleaseImage(detectThrs);
                    double mom10 = cvGetSpatialMoment(moments, 1, 0);
                    double mom01 = cvGetSpatialMoment(moments, 0, 1);
                    double area = cvGetCentralMoment(moments, 0, 0);
                    posX = (int) (mom10 / area);
                    posY = (int) (mom01 / area);
                    // only if its a valid position
                    if (posX > 0 && posY > 0) {
                        paint(img, posX, posY);
                        ctr=0;
                    }
                    else
                    {
                    	ctr++;
                    }
                }
                // Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
        	System.out.print(e.getMessage());
        }
    }

    private void paint(IplImage img, int posX, int posY) {
    	ctr++;
        Graphics g = jp.getGraphics();
        Graphics g2 = imgGenerator.getGraphics();
        path.setSize(img.width(), img.height());
        // g.clearRect(0, 0, img.width(), img.height());
        g.setColor(Color.RED);
        g2.setColor(Color.RED);
        // g.fillOval(posX, posY, 20, 20);
        if(pposX+pposY!=0&&ctr<climit)
        {
        	ctr=0;
        	fillBetween(posX,posY,pposX,pposY,g);
        	fillBetween(posX,posY,pposX,pposY,g2);
        	pposX=posX;
        	pposY=posY;
        }
        else
        {
        	g.fillOval(posX, posY, 1, 1);
        	g2.fillOval(posX, posY, 1, 1);
        	pposX=posX;pposY=posY;
        }
        System.out.println(posX + " , " + posY);
    }
    private void fillBetween(int x1, int y1, int x2, int y2, Graphics g)
    {
    	g.drawLine(x1, y1, x2, y2);
    }
    private void testFunction()
    {
    
    }
    private IplImage getThresholdImage(IplImage orgImg) {
    	
    	IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
    	
        cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red
        //canvas.showImage(imgThreshold);
        //cvSmooth(imgThreshold,imgThreshold,CV_BLUR,3);
        //cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 9);
        cvSmooth(imgThreshold, imgThreshold, CV_GAUSSIAN, 15);//Mine
        //cvSaveImage(++ii + "dsmthreshold.jpg", imgThreshold);
        return imgThreshold;
    }

    public static void main(String[] args) {
        ColoredObjectTrack cot = new ColoredObjectTrack();
        Thread th = new Thread(cot);
        th.start();
    }

    public IplImage Equalize(BufferedImage bufferedimg) {
        IplImage iploriginal = IplImage.createFrom(bufferedimg);
        IplImage srcimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        IplImage destimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
       // cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY); //me commented
       // cvCvtColor(iploriginal,srcimg,CV_BGR2HSV); //me commented
        cvEqualizeHist(srcimg, destimg);
        return destimg;
    }
    public BufferedImage blacken(BufferedImage img,boolean fill)
    {
    	/*for(int i =0;i<black[0].length;i++)
    	{
    		System.out.println("BX:"+black[0][i]+"BY:"+black[1][i]);
    		img.setRGB(black[0][i], black[1][i], 16777215);
    	}
    	return img;*/
    	if(rect!=null)
    	{
    		Graphics g = img.getGraphics();
    		for(int i =0;i<rectRowCtr;i++)
    		{
    				g.fillRect(rect[i][0]-20, rect[i][1]-20, rect[i][2]-rect[i][0], rect[i][3]-rect[i][1]);
    		}
    	}
    	return img;
    }
    public void findBlack(BufferedImage img)
    {
    	/*black = new int[2][];
    	ArrayList<Integer> x = new ArrayList<Integer>(100);
    	ArrayList<Integer> y = new ArrayList<Integer>(100);
    	System.out.println("X"+img.getRGB(0, 0));
    	for(int i=0;i<img.getHeight();i++)
    	{
    		for(int j=0;j<img.getWidth();j++)
    		{

    			if(img.getRGB(j, i)>-16777216)
    			{
    				System.out.println("I:"+i+"J:"+j);
    				x.add(j);
    				y.add(i);
    			}
    		}
    	}
    	int[] tx = new int[x.size()];
    	int[] ty = new int[y.size()];
    	for(int i=0;i<tx.length;i++)
    	{
    		tx[i]=x.get(i);
    		ty[i]=y.get(i);
    	}
    	black[0]=tx;
    	black[1]=ty;*/
    }
}