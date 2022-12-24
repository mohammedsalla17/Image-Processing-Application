//Mohammad Ibrahim Salla (180807655)
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class Demo extends Component implements ActionListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original",
            "Negative",
            "Rescale",
            "Shift",
            "Shift & Rescale",
            "Arithmetic Operations",
            "Bitwise NOT",
            "Bitwise Operations",
            "Negative Linear Transform",
            "Logarithmic Functions",
            "Power-Law",
            "Random Look-up Table",
            "Bit-Plane Slicing",
            "Image Convolution",
            "Salt and Pepper Noise",
            "Minimum Filter",
            "Maximum Filter",
            "Midpoint Filter",
            "Median Filter",

    };

    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage bi, biSecond, biFiltered, biPrevious;   // the input image saved as bi;//
    int w, h;

    public Demo() {
        try {
            bi = ImageIO.read(new File("images/Baboon.bmp"));

            w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
                biSecond = ImageIO.read(new File("images/Barbara.bmp"));
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }


    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }



    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();
        g.drawImage(biFiltered, 0, 0, null);
        g.drawImage(biSecond, bi.getWidth(), 0, null);
    }

    public static void Mainmenu(JFrame frame, Demo d){
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        JMenu file = new JMenu("Edit");
        menubar.add(file);
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(event-> {
            d.Functionundo();
        });
        file.add(undo);
    }

        public void Functionundo(){
            biFiltered = biPrevious;
            repaint();
        }
    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] ImageArray = convertToArray(img);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  Image Rescale
    //************************************

    public BufferedImage ImageRescale(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        double rescaleVal = 0;
        rescaleVal = Double.parseDouble(JOptionPane.showInputDialog("Enter a value for the Image rescale:"));
        if(rescaleVal > 2 || rescaleVal < 0) {
            JOptionPane.showMessageDialog(null, "Error: Input a rescale value between 0 and 2");
            return img;
        }

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                imageArray[x][y][1] = (int) Math.round(rescaleVal * imageArray[x][y][1]);
                imageArray[x][y][2] = (int) Math.round(rescaleVal * imageArray[x][y][2]);
                imageArray[x][y][3] = (int) Math.round(rescaleVal * imageArray[x][y][3]);
            }
        }

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if (imageArray[x][y][1] < 0) imageArray[x][y][1] = 0;
                if (imageArray[x][y][2] < 0) imageArray[x][y][2] = 0;
                if (imageArray[x][y][3] < 0) imageArray[x][y][3] = 0;
                if (imageArray[x][y][1] > 255) imageArray[x][y][1] = 255;
                if (imageArray[x][y][2] > 255) imageArray[x][y][2] = 255;
                if (imageArray[x][y][3] > 255) imageArray[x][y][3] = 255;
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Image Shifting
    //************************************

    public BufferedImage ImageShifting(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        int shiftVal = 0;
        shiftVal = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for the Image shifting:"));
        if(shiftVal > 50 || shiftVal < -50) {
            JOptionPane.showMessageDialog(null, "Error: Input a Shifting value between -50 and 50");
            return img;
        }

        for(int y=0; y<height; y++){
            for (int x = 0; x < width; x++) {
                imageArray[x][y][1] = (imageArray[x][y][1] + shiftVal);
                imageArray[x][y][2] = (imageArray[x][y][2] + shiftVal);
                imageArray[x][y][3] = (imageArray[x][y][3] + shiftVal);
            }
        }

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if (imageArray[x][y][1] < 0) imageArray[x][y][1] = 0;
                if (imageArray[x][y][2] < 0) imageArray[x][y][2] = 0;
                if (imageArray[x][y][3] < 0) imageArray[x][y][3] = 0;
                if (imageArray[x][y][1] > 255) imageArray[x][y][1] = 255;
                if (imageArray[x][y][2] > 255) imageArray[x][y][2] = 255;
                if (imageArray[x][y][3] > 255) imageArray[x][y][3] = 255;
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Image Rescaling and Shifting
    //************************************

    public BufferedImage ImageRescaleShifting(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);
        double maxVal = 0;
        double minVal = 255;

        double rescaleVal = 0;
        rescaleVal = Double.parseDouble(JOptionPane.showInputDialog("Enter a value for the Image rescale:"));
        if(rescaleVal > 2 || rescaleVal < 0) {
            JOptionPane.showMessageDialog(null, "Error: Input a rescale value between 0 and 2");
            return img;
        }

        int shiftVal = 0;
        shiftVal = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for the Image shifting:"));
        if(shiftVal > 50 || shiftVal < -50) {
            JOptionPane.showMessageDialog(null, "Error: Input a Shifting value between -50 and 50");
            return img;
        }

        Random rand = new Random();
        int randVal = rand.nextInt(50);

        for(int y=0; y<height; y++){
            for (int x = 0; x < width; x++) {
                imageArray[x][y][1] = (int) Math.round(rescaleVal * ((imageArray[x][y][1] + randVal) + shiftVal));
                imageArray[x][y][2] = (int) Math.round(rescaleVal * ((imageArray[x][y][2] + randVal) + shiftVal));
                imageArray[x][y][3] = (int) Math.round(rescaleVal * ((imageArray[x][y][3] + randVal) + shiftVal));
                if(imageArray[x][y][1] > maxVal) {maxVal = imageArray[x][y][1];}
                if(imageArray[x][y][1] < minVal) {minVal = imageArray[x][y][1];}
                if(imageArray[x][y][2] > maxVal) {maxVal = imageArray[x][y][2];}
                if(imageArray[x][y][2] < minVal) {minVal = imageArray[x][y][2];}
                if(imageArray[x][y][3] > maxVal) {maxVal = imageArray[x][y][3];}
                if(imageArray[x][y][3] < minVal) {minVal = imageArray[x][y][3];}
            }
        }

        for (int y=0; y<height; y++){
            for (int x = 0; x < width; x++) {
                imageArray[x][y][1] = (int) (Math.round((imageArray[x][y][1] - minVal)/(maxVal - minVal) * 255));
                imageArray[x][y][2] = (int) (Math.round((imageArray[x][y][2] - minVal)/(maxVal - minVal) * 255));
                imageArray[x][y][3] = (int) (Math.round((imageArray[x][y][3] - minVal)/(maxVal - minVal) * 255));
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Arithmetic Operations
    //************************************

   public BufferedImage ArithmeticOperation(BufferedImage img, BufferedImage img2) {
       biPrevious = biFiltered;
       int width = img.getWidth();
       int height = img.getHeight();

       int[][][] imageArray = convertToArray(img);
       int[][][] imageArray2 = convertToArray(img2);
       int[][][] imageArrayout = new int[width][height][4];

       double maxVal = 0;
       double minVal = 255;

       String[] options = { "add", "subtract", "multiply", "divide" };
       JComboBox comboBox = new JComboBox(options);

       JOptionPane.showInputDialog(null, "Choose", "Menu", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

       if(comboBox.getSelectedItem() == null) {
           return img2;
       }

       for(int y=0; y<height; y++) {
           for(int x=0; x<width; x++) {
               if(comboBox.getSelectedItem().equals("subtract")) {
                   imageArrayout[x][y][1] = imageArray[x][y][1] - imageArray2[x][y][1];
                   imageArrayout[x][y][2] = imageArray[x][y][2] - imageArray2[x][y][2];
                   imageArrayout[x][y][3] = imageArray[x][y][3] - imageArray2[x][y][3];
               }
               if(comboBox.getSelectedItem().equals("add")) {
                   imageArrayout[x][y][1] = imageArray[x][y][1] + imageArray2[x][y][1];
                   imageArrayout[x][y][2] = imageArray[x][y][2] + imageArray2[x][y][2];
                   imageArrayout[x][y][3] = imageArray[x][y][3] + imageArray2[x][y][3];
               }
               if(comboBox.getSelectedItem().equals("multiply")) {
                   imageArrayout[x][y][1] = imageArray[x][y][1] * imageArray2[x][y][1];
                   imageArrayout[x][y][2] = imageArray[x][y][2] * imageArray2[x][y][2];
                   imageArrayout[x][y][3] = imageArray[x][y][3] * imageArray2[x][y][3];
               }
               if(comboBox.getSelectedItem().equals("divide")) {
                   if(imageArray2[x][y][1] != 0) {
                       imageArrayout[x][y][1] = imageArray[x][y][1] / imageArray2[x][y][1];
                   }
                   if(imageArray2[x][y][1] == 0){
                       imageArrayout[x][y][1] = 255;
                   }
                   if(imageArray2[x][y][2] != 0) {
                       imageArrayout[x][y][2] = imageArray[x][y][2] / imageArray2[x][y][2];
                   }
                   if(imageArray2[x][y][2] == 0){
                       imageArrayout[x][y][2] = 255;
                   }
                   if(imageArray2[x][y][3] != 0) {
                       imageArrayout[x][y][3] = imageArray[x][y][3] / imageArray2[x][y][3];
                   }
                   if(imageArray2[x][y][3] == 0){
                       imageArrayout[x][y][1] = 255;
                   }
               }
               if(imageArrayout[x][y][1] > maxVal) {
                   maxVal = imageArrayout[x][y][1];
               }
               if(imageArrayout[x][y][2] > maxVal) {
                   maxVal = imageArrayout[x][y][2];
               }
               if(imageArrayout[x][y][3] > maxVal) {
                   maxVal = imageArrayout[x][y][3];
               }
               if(imageArrayout[x][y][1] < minVal) {
                   minVal = imageArrayout[x][y][1];
               }
               if(imageArrayout[x][y][2] < minVal) {
                   minVal = imageArrayout[x][y][2];
               }
               if(imageArrayout[x][y][3] < minVal) {
                   minVal = imageArrayout[x][y][3];
               }
           }
       }
       for(int y=0; y<height; y++) {
           for(int x=0; x<width; x++) {
               imageArrayout[x][y][1] = (int)(Math.round((imageArrayout[x][y][1] - minVal)/(maxVal - minVal) * 255));
               imageArrayout[x][y][2] = (int)(Math.round((imageArrayout[x][y][2] - minVal)/(maxVal - minVal) * 255));
               imageArrayout[x][y][3] = (int)(Math.round((imageArrayout[x][y][3] - minVal)/(maxVal - minVal) * 255));
           }
       }
       return convertToBimage(imageArrayout);
   }

    //************************************
    //  Image Bitwise Not
    //************************************

    public BufferedImage ImageBitwiseNot(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                imageArray[x][y][1] = ~imageArray[x][y][1]&0xff;
                imageArray[x][y][2] = ~imageArray[x][y][2]&0xff;
                imageArray[x][y][3] = ~imageArray[x][y][3]&0xff;
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Image Bitwise Operations
    //************************************

    public BufferedImage BitwiseOperations(BufferedImage img, BufferedImage img2) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();

        int[][][] imageArray = convertToArray(img);
        int[][][] imageArray2 = convertToArray(img2);
        int[][][] imageArrayOut = new int[width][height][4];

        String[] options = { "AND", "OR", "XOR"};
        JComboBox comboBox = new JComboBox(options);
        JOptionPane.showInputDialog(null, "Choose", "Menu", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if(comboBox.getSelectedItem() == null) {
            return img2;
        }

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if(comboBox.getSelectedItem().equals("AND")) {
                    imageArrayOut[x][y][1] = imageArray[x][y][1]&0xff & imageArray2[x][y][1]&0xff;
                    imageArrayOut[x][y][2] = imageArray[x][y][2]&0xff & imageArray2[x][y][2]&0xff;
                    imageArrayOut[x][y][3] = imageArray[x][y][3]&0xff & imageArray2[x][y][3]&0xff;
                }
                else if(comboBox.getSelectedItem().equals("OR")) {
                    imageArrayOut[x][y][1] = imageArray[x][y][1] | imageArray2[x][y][1]&0xff;
                    imageArrayOut[x][y][2] = imageArray[x][y][2] | imageArray2[x][y][2]&0xff;
                    imageArrayOut[x][y][3] = imageArray[x][y][3] | imageArray2[x][y][3]&0xff;
                }
                else if(comboBox.getSelectedItem().equals("XOR")) {
                    imageArrayOut[x][y][1] = imageArray[x][y][1] ^ imageArray2[x][y][1]&0xff;
                    imageArrayOut[x][y][2] = imageArray[x][y][2] ^ imageArray2[x][y][2]&0xff;
                    imageArrayOut[x][y][3] = imageArray[x][y][3] ^ imageArray2[x][y][3]&0xff;
                }
            }
        }
        return convertToBimage(imageArrayOut);
    }

    //************************************
    //  Negative Linear Transformation
    //************************************

    public BufferedImage NegativeLinearTransformation(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int L = 256;
        int[][][] imageArray = convertToArray(img);

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                imageArray[x][y][1] = L - 1 - imageArray[x][y][1];
                imageArray[x][y][2] = L - 1 - imageArray[x][y][2];
                imageArray[x][y][3] = L - 1 - imageArray[x][y][3];
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Logarithmic Functions
    //************************************

    public BufferedImage LogarithmicFunction(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        int c = 0;
        c = Integer.parseInt(JOptionPane.showInputDialog("Enter a constant value for the Logarithmic Function:"));

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                imageArray[x][y][1] = (int) (c * Math.log10(imageArray[x][y][1]));
                imageArray[x][y][2] = (int) (c * Math.log10(imageArray[x][y][2]));
                imageArray[x][y][3] = (int) (c * Math.log10(imageArray[x][y][3]));
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Power-Law
    //************************************

    public BufferedImage PowerLaw(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        double c = 0;
        c = Double.parseDouble(JOptionPane.showInputDialog("Enter a constant value for the Power Law:"));

        double p = 0;
        p = Double.parseDouble(JOptionPane.showInputDialog("Enter a value for the power p:"));
        if(p > 25 || p < 0.01) {
            JOptionPane.showMessageDialog(null, "Error: Input a power p value between 0.01 and 25");
            return img;
        }

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                imageArray[x][y][1] = (int) (c * Math.pow(imageArray[x][y][1], p));
                imageArray[x][y][2] = (int) (c * Math.pow(imageArray[x][y][2], p));
                imageArray[x][y][3] = (int) (c * Math.pow(imageArray[x][y][3], p));
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Random Look-up Table
    //************************************

    public BufferedImage RandomLookupTable(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);
        int[] LUT = new int[256];
        Random random = new Random();

        for(int x = 0; x <= 255; x++) {
            LUT[x] = random.nextInt(255);
        }

        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                imageArray[x][y][1] = LUT[imageArray[x][y][1]];
                imageArray[x][y][2] = LUT[imageArray[x][y][2]];
                imageArray[x][y][3] = LUT[imageArray[x][y][3]];
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Bit-plane slicing
    //************************************

    public BufferedImage Bitplaneslicing(BufferedImage img) {
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        int bit = 0;
        bit = Integer.parseInt(JOptionPane.showInputDialog("Enter a bit value:"));
        if(bit > 7 || bit < 0) {
            JOptionPane.showMessageDialog(null, "Error: Input a bit value between 0 and 7");
            return img;
        }

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                int r = imageArray[x][y][1];
                int g = imageArray[x][y][2];
                int b = imageArray[x][y][3];

                imageArray[x][y][1] = ((r>>bit) & 1)*255;
                imageArray[x][y][2] = ((g>>bit) & 1)*255;
                imageArray[x][y][3] = ((b>>bit) & 1)*255;
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Image Convolution
    //************************************

    public BufferedImage ImageConvolution(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);
        int[][][] imageArrayOut = new int[width][height][4];

        float[][] Mask = new float[3][3];
        float[][] Mask2 = new float[3][3];

        int option = 0;
        option = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for the case you want to apply:"));
        if(option > 9 || option < 1) {
            JOptionPane.showMessageDialog(null, "Error: Input a case value between 1 and 9");
            return img;
        }


        switch(option) {
            //Averaging
            case 1:
                Mask[0][0] = 1;
                Mask[0][1] = 1;
                Mask[0][2] = 1;
                Mask[1][0] = 1;
                Mask[1][1] = 1;
                Mask[1][2] = 1;
                Mask[2][0] = 1;
                Mask[2][1] = 1;
                Mask[2][2] = 1;
                break;

            //Weighted averaging
            case 2:
                Mask[0][0] = 1;
                Mask[0][1] = 2;
                Mask[0][2] = 1;
                Mask[1][0] = 2;
                Mask[1][1] = 4;
                Mask[1][2] = 2;
                Mask[2][0] = 1;
                Mask[2][1] = 2;
                Mask[2][2] = 1;
                break;

            //4-neighbour Laplacian
            case 3:
                Mask[0][0] = 0;
                Mask[0][1] = -1;
                Mask[0][2] = 0;
                Mask[1][0] = -1;
                Mask[1][1] = 4;
                Mask[1][2] = -1;
                Mask[2][0] = 0;
                Mask[2][1] = -1;
                Mask[2][2] = 0;
                break;

            //8-neighbour Laplacian
            case 4:
                Mask[0][0] = -1;
                Mask[0][1] = -1;
                Mask[0][2] = -1;
                Mask[1][0] = -1;
                Mask[1][1] = 8;
                Mask[1][2] = -1;
                Mask[2][0] = -1;
                Mask[2][1] = -1;
                Mask[2][2] = -1;
                break;

            //4-neighbour Laplacian Enhancement
            case 5:
                Mask[0][0] = 0;
                Mask[0][1] = -1;
                Mask[0][2] = 0;
                Mask[1][0] = -1;
                Mask[1][1] = 5;
                Mask[1][2] = -1;
                Mask[2][0] = 0;
                Mask[2][1] = -1;
                Mask[2][2] = 0;
                break;

            //8-neighbour Laplacian Enhancement
            case 6:
                Mask[0][0] = -1;
                Mask[0][1] = -1;
                Mask[0][2] = -1;
                Mask[1][0] = -1;
                Mask[1][1] = 9;
                Mask[1][2] = -1;
                Mask[2][0] = -1;
                Mask[2][1] = -1;
                Mask[2][2] = -1;
                break;

            //Roberts
            case 7:
                Mask[0][0] = 0;
                Mask[0][1] = 0;
                Mask[0][2] = 0;
                Mask[1][0] = 0;
                Mask[1][1] = 0;
                Mask[1][2] = -1;
                Mask[2][0] = 0;
                Mask[2][1] = 1;
                Mask[2][2] = 0;

                Mask2[0][0] = 0;
                Mask2[0][1] = 0;
                Mask2[0][2] = 0;
                Mask2[1][0] = 0;
                Mask2[1][1] = -1;
                Mask2[1][2] = 0;
                Mask2[2][0] = 0;
                Mask2[2][1] = 0;
                Mask2[2][2] = 1;
                break;

            //Sobel X
            case 8:
                Mask[0][0] = -1;
                Mask[0][1] = 0;
                Mask[0][2] = 1;
                Mask[1][0] = -2;
                Mask[1][1] = 0;
                Mask[1][2] = 2;
                Mask[2][0] = -1;
                Mask[2][1] = 0;
                Mask[2][2] = 1;
                break;

            //Sobel Y
            case 9:
                Mask[0][0] = -1;
                Mask[0][1] = -2;
                Mask[0][2] = -1;
                Mask[1][0] = 0;
                Mask[1][1] = 0;
                Mask[1][2] = 0;
                Mask[2][0] = 1;
                Mask[2][1] = 2;
                Mask[2][2] = 1;
                break;
        }

        double maxVal = -500;
        double minVal = 500;
        for(int y=1; y<height-1; y++) {
            for(int x=1; x<width-1; x++) {
                double pixelVal = 0;
                double pixel2ndVal = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        pixelVal += Mask[1-s][1-t] * imageArray[x+s][y+t][1];
                        pixelVal += Mask[1-s][1-t] * imageArray[x+s][y+t][2];
                        pixelVal += Mask[1-s][1-t] * imageArray[x+s][y+t][3];
                        if(option == 7){
                            pixel2ndVal += Mask2[1-s][1-t] * imageArray[x+s][y+t][1];
                            pixel2ndVal += Mask2[1-s][1-t] * imageArray[x+s][y+t][2];
                            pixel2ndVal += Mask2[1-s][1-t] * imageArray[x+s][y+t][3];
                        }
                    }
                }
                switch(option) {
                    case 1: pixelVal = pixelVal/9;
                        break;
                    case 2: pixelVal = pixelVal/16;
                        break;
                    case 7: pixelVal = Math.abs((int)pixelVal) + Math.abs((int)pixel2ndVal);
                        break;
                    case 8: pixelVal = Math.abs((int)pixelVal);
                        break;
                    case 9: pixelVal = Math.abs((int)pixelVal);
                        break;
                    default:
                        break;
                }
                imageArrayOut[x][y][1] = (int) Math.round(pixelVal);
                imageArrayOut[x][y][2] = (int) Math.round(pixelVal);
                imageArrayOut[x][y][3] = (int) Math.round(pixelVal);
                if(pixelVal > maxVal) {
                    maxVal = pixelVal;
                }
                else if(pixelVal < minVal) {
                    minVal = pixelVal;
                }
            }
        }

        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                imageArrayOut[x][y][1] = (int)((imageArrayOut[x][y][1]-minVal)/(maxVal-minVal)*255);
                imageArrayOut[x][y][2] = (int)((imageArrayOut[x][y][2]-minVal)/(maxVal-minVal)*255);
                imageArrayOut[x][y][3] = (int)((imageArrayOut[x][y][3]-minVal)/(maxVal-minVal)*255);
            }
        }
        return convertToBimage(imageArrayOut);
    }

    //************************************
    //  Add Salt-and-Pepper Noise
    //************************************

    public BufferedImage AddSaltPepperNoise(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int[][][] imageArray = convertToArray(img);
        int y=0;
        Random rand = new Random();
        for(int x=0; x<width; x++) {
            double randVal = rand.nextDouble();
            y = rand.nextInt(256);
            if(randVal >= 0.5) {
                imageArray[x][y][1] = 255;
                imageArray[x][y][2] = 255;
                imageArray[x][y][3] = 255;
            }
            else if(randVal < 0.5) {
                imageArray[x][y][1] = 0;
                imageArray[x][y][2] = 0;
                imageArray[x][y][3] = 0;
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Minimum Filtering
    //************************************

    public BufferedImage MinimumFilter(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);
        int[][][] imageArrayout = convertToArray(img);

        for(int y=1; y<height-1; y++) {
            for(int x=1; x<width-1; x++) {
                int rminVal = 255;
                int gminVal = 255;
                int bminVal = 255;
                for(int s=-1; s<=1; s++) {
                    for(int t=-1; t<=1; t++) {
                        if(imageArray[x+s][y+t][1] < rminVal) {
                            rminVal = imageArray[x+s][y+t][1];
                        }
                        if(imageArray[x+s][y+t][2] < gminVal) {
                            gminVal = imageArray[x+s][y+t][2];
                        }
                        if(imageArray[x+s][y+t][3] < bminVal) {
                            bminVal = imageArray[x+s][y+t][3];
                        }
                    }
                }
                imageArrayout[x][y][1] = rminVal;
                imageArrayout[x][y][2] = gminVal;
                imageArrayout[x][y][3] = bminVal;
            }
        }
        return convertToBimage(imageArrayout);
    }

    //************************************
    //  Maximum Filtering
    //************************************

    public BufferedImage MaximumFilter(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);
        int[][][] imageArrayout = convertToArray(img);

        for(int y=1; y<height-1; y++) {
            for(int x=1; x<width-1; x++) {
                int rmaxVal = 0;
                int gmaxVal = 0;
                int bmaxVal = 0;
                for(int s=-1; s<=1; s++) {
                    for(int t=-1; t<=1; t++) {
                        if(imageArray[x+s][y+t][1] > rmaxVal) {
                            rmaxVal = imageArray[x+s][y+t][1];
                        }
                        if(imageArray[x+s][y+t][2] > gmaxVal) {
                            gmaxVal = imageArray[x+s][y+t][2];
                        }
                        if(imageArray[x+s][y+t][3] > bmaxVal) {
                            bmaxVal = imageArray[x+s][y+t][3];
                        }
                    }
                }
                imageArrayout[x][y][1] = rmaxVal;
                imageArrayout[x][y][2] = gmaxVal;
                imageArrayout[x][y][3] = bmaxVal;
            }
        }
        return convertToBimage(imageArrayout);
    }

    //************************************
    //  Midpoint Filtering
    //************************************

    public BufferedImage MidpointFilter(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        for(int y=1; y<height-1; y++) {
            for(int x=1; x<width-1; x++) {
                int maxVal = 0;
                int minVal = 0;
                for(int s=-1; s<=1; s++) {
                    for(int t=-1; t<=1; t++) {
                        if(imageArray[x+s][y+t][1] > maxVal) {
                            maxVal = imageArray[x+s][y+t][1];
                        }
                        if(imageArray[x+s][y+t][2] > maxVal) {
                            maxVal = imageArray[x+s][y+t][2];
                        }
                        if(imageArray[x+s][y+t][3] > maxVal) {
                            maxVal = imageArray[x+s][y+t][3];
                        }
                        if(imageArray[x+s][y+t][1] < minVal) {
                            minVal = imageArray[x+s][y+t][1];
                        }
                        if(imageArray[x+s][y+t][2] < minVal) {
                            minVal = imageArray[x+s][y+t][2];
                        }
                        if(imageArray[x+s][y+t][3] < minVal) {
                            minVal = imageArray[x+s][y+t][3];
                        }
                    }
                }
                imageArray[x][y][1] = ((maxVal-minVal)/2);
                imageArray[x][y][2] = ((maxVal-minVal)/2);
                imageArray[x][y][3] = ((maxVal-minVal)/2);
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  Median Filtering
    //************************************

    public BufferedImage MedianFilter(BufferedImage img){
        biPrevious = biFiltered;
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] imageArray = convertToArray(img);

        for(int y=1; y<height-1; y++) {
            for(int x=1; x<width-1; x++) {
                int[] window = new int[9];
                int i = 0;
                for(int s=-1; s<=1; s++) {
                    for(int t=-1; t<=1; t++) {
                        window[i] = imageArray[x+s][y+t][1];
                        window[i] = imageArray[x+s][y+t][2];
                        window[i] = imageArray[x+s][y+t][3];
                        i++;
                    }
                }
                Arrays.sort(window);
                imageArray[x][y][1] = window[(window.length+1)/2];
                imageArray[x][y][2] = window[(window.length+1)/2];
                imageArray[x][y][3] = window[(window.length+1)/2];
            }
        }
        return convertToBimage(imageArray);
    }

    //************************************
    //  You need to register your function here
    //************************************
    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2: biFiltered = ImageRescale(bi); /* Image Rescale */
                return;
            case 3: biFiltered = ImageShifting(bi); /* Image Shifting */
                return;
            case 4: biFiltered = ImageRescaleShifting(bi); /* Image Rescale & Shifting */
                return;
            case 5: biFiltered = ArithmeticOperation(bi, biSecond); /* Arithmetic Operation */
                return;
            case 6: biFiltered = ImageBitwiseNot(bi); /* Image Bitwise Not */
                return;
            case 7: biFiltered = BitwiseOperations(bi, biSecond); /* Image Bitwise Operations */
                return;
            case 8: biFiltered = NegativeLinearTransformation(bi); /* Negative Linear Transformation */
                return;
            case 9: biFiltered = LogarithmicFunction(bi); /* Logarithmic Function */
                return;
            case 10: biFiltered = PowerLaw(bi); /* Power Law */
                return;
            case 11: biFiltered = RandomLookupTable(bi); /* Random Look-up Table */
                return;
            case 12: biFiltered = Bitplaneslicing(bi); /* Bit-plane Slicing */
                return;
            case 13: biFiltered = ImageConvolution(bi); /* Image Convolution */
                return;
            case 14: biFiltered = AddSaltPepperNoise(bi); /* Salt-and-Pepper Noise */
                return;
            case 15: biFiltered = MinimumFilter(bi); /* Minimum Filtering */
                return;
            case 16: biFiltered = MaximumFilter(bi); /* Maximum Filtering */
                return;
            case 17: biFiltered = MidpointFilter(bi); /* Midpoint Filtering */
                return;
            case 18: biFiltered = MedianFilter(bi); /* Median Filtering */
            //************************************
            // case 2:
            //      return;
            //************************************

        }

    }



    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        if (cb.getActionCommand().equals("SetFilter")) {
            setOpIndex(cb.getSelectedIndex());
            repaint();
        } else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        }
    };

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo de = new Demo();
        Mainmenu(f, de);
        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);

        Demo img2 = new Demo();
        f.add("West", img2);
        f.pack();
        f.setVisible(true);

    }
}