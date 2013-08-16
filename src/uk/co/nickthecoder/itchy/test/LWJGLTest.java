/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class LWJGLTest
{
    public static final ColorModel glAlphaColorModel =
        new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[] { 8, 8, 8, 8 },
            true,
            false,
            ComponentColorModel.TRANSLUCENT,
            DataBuffer.TYPE_BYTE);

    /** The colour model for the GL image */
    public static final ColorModel glColorModel =
        new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[] { 8, 8, 8, 0 },
            false,
            false,
            ComponentColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE);
    
    public class ImageData
    {
        /** The bit depth of the image */
        public int depth;
        /** The height of the image */
        public int height;
        /** The width of the image */
        public int width;
        /** The width of the texture that should be created for the image */
        public int texWidth;
        /** The height of the texture that should be created for the image */
        public int texHeight;
        /** True if we should edge */
        public boolean edging = true;

        
        public void load( String filename ) throws IOException
        {
            FileInputStream fis = new FileInputStream( filename );
            createTexture( fis, GL11.GL_LINEAR,GL11. GL_LINEAR, false );
        }

        public int textureID;
        
        /**
         * Get a texture from a image file
         * 
         * @param in The stream from which we can load the image
         * @param resourceName The name to give this image in the internal cache
         * @param flipped True if we should flip the image on the y-axis while loading
         * @param target The texture target we're loading this texture into
         * @param minFilter The scaling down filter
         * @param magFilter The scaling up filter
         * @param transparent The colour to interpret as transparent or null if none
         * @return The texture loaded
         * @throws IOException Indicates a failure to load the image
         */
        private void createTexture( InputStream in, 
                                  int magFilter, 
                                  int minFilter, boolean flipped ) throws IOException 
        { 
            // create the texture ID for this texture 
            ByteBuffer textureBuffer = loadImage( in, flipped, false );

            IntBuffer ids = BufferUtils.createIntBuffer(1);
            GL11.glGenTextures(ids);
            textureID = ids.get(0);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); 

            boolean hasAlpha = this.depth == 32;

            int srcPixelFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
            
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
            
            // produce a texture from the byte buffer
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 
                          0, 
                          GL11.GL_RGBA8, 
                          this.texWidth, 
                          this.texHeight, 
                          0, 
                          srcPixelFormat, 
                          GL11.GL_UNSIGNED_BYTE, 
                          textureBuffer); 
        } 

        public ByteBuffer loadImage( InputStream fis, boolean flipped, boolean forceAlpha ) throws IOException
        {
            BufferedImage bufferedImage = ImageIO.read(fis);
            return imageToByteBuffer(bufferedImage, flipped, forceAlpha );
        }

        public ByteBuffer imageToByteBuffer( BufferedImage image, boolean flipped,
            boolean forceAlpha )
        {
            ByteBuffer imageBuffer = null;
            WritableRaster raster;
            BufferedImage texImage;

            int texWidth = 1;
            int texHeight = 1;

            // find the closest power of 2 for the width and height
            // of the produced texture

            while (texWidth < image.getWidth()) {
                texWidth *= 2;
            }
            while (texHeight < image.getHeight()) {
                texHeight *= 2;
            }

            this.width = image.getWidth();
            this.height = image.getHeight();
            this.texHeight = texHeight;
            this.texWidth = texWidth;

            // create a raster that can be used by OpenGL as a source
            // for a texture
            boolean useAlpha = image.getColorModel().hasAlpha() || forceAlpha;

            if (useAlpha) {
                this.depth = 32;
                raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight,
                    4, null);
                texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable<String,Object>());
            } else {
                this.depth = 24;
                raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight,
                    3, null);
                texImage = new BufferedImage(glColorModel, raster, false, new Hashtable<String,Object>());
            }

            // copy the source image into the produced image
            Graphics2D g = (Graphics2D) texImage.getGraphics();

            // only need to blank the image for mac compatibility if we're using alpha
            if (useAlpha) {
                g.setColor(new Color(0f, 0f, 0f, 0f));
                g.fillRect(0, 0, texWidth, texHeight);
            }

            if (flipped) {
                g.scale(1, -1);
                g.drawImage(image, 0, -this.height, null);
            } else {
                g.drawImage(image, 0, 0, null);
            }

            if (this.edging) {
                if (this.height < texHeight - 1) {
                    copyArea(texImage, 0, 0, this.width, 1, 0, texHeight - 1);
                    copyArea(texImage, 0, this.height - 1, this.width, 1, 0, 1);
                }
                if (this.width < texWidth - 1) {
                    copyArea(texImage, 0, 0, 1, this.height, texWidth - 1, 0);
                    copyArea(texImage, this.width - 1, 0, 1, this.height, 1, 0);
                }
            }

            // build a byte buffer from the temporary image
            // that be used by OpenGL to produce a texture.
            byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

            imageBuffer = ByteBuffer.allocateDirect(data.length);
            imageBuffer.order(ByteOrder.nativeOrder());
            imageBuffer.put(data, 0, data.length);
            imageBuffer.flip();
            g.dispose();

            return imageBuffer;
        }

        private void copyArea(BufferedImage image, int x, int y, int width, int height, int dx, int dy) {
            Graphics2D g = (Graphics2D) image.getGraphics();
            
            g.drawImage(image.getSubimage(x, y, width, height),x+dx,y+dy,null);
        }
    }

    /**
     * Start the example
     */
    public void start() throws Exception
    {
        initGL(800, 600);
        init();

        while (true) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            render();

            Display.update();
            Display.sync(100);

            if (Display.isCloseRequested()) {
                Display.destroy();
                System.exit(0);
            }
        }
    }

    /**
     * Initialise the GL display
     * 
     * @param width
     *        The width of the display
     * @param height
     *        The height of the display
     */
    private void initGL( int width, int height )
    {
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // enable alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, 0, height, 1, 0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

    }

    private ImageData imageData;
    
    /**
     * Initialise resources
     */
    public void init() throws Exception
    {

        try {
            // load texture from PNG file
            imageData = new ImageData();
            imageData.load( "resources/drunkInvaders/alien1a.png" );
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * draw a quad with the image on it
     */
    public void render()
    {
        // this.texture.bind(); // or GL11.glBind(texture.getTextureID());

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageData.textureID );

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(100, 100);
        
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(100,228);
        
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(228,228);
        
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(228,100);
        GL11.glEnd();
    }

    /**
     * Main Class
     */
    public static void main( String[] argv ) throws Exception
    {
        LWJGLTest test = new LWJGLTest();
        test.start();
    }
}
