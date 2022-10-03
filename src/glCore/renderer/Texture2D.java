package glCore.renderer;

import glCore.core.Ref;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Texture2D extends Ref implements IRendererResource {

    private final int _rendererID;
    private final int _width;
    private final int _height;
    private final int _nrComp;

    private boolean _isValid;

    private String _filepath = "N/A";

    public int _bindSlot = 0;

    // set width, height, nrComp, -1 to used loaded image value
    public Texture2D(String filename, boolean flipImageY, int minFilter, int magFilter, int wrapS, int wrapT){
        _filepath = filename;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(flipImageY);

            ByteBuffer image = stbi_load(filename, w, h, comp, 0);
            assert image != null;

            _nrComp = comp.get();
            _width = w.get();
            _height = h.get();

            int format = getColorFormat(_nrComp);
            _rendererID = GL33.glGenTextures();
            GL33.glActiveTexture(GL33.GL_TEXTURE0);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, _rendererID);
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, format, _width, _height, 0,
                    format, GL33.GL_UNSIGNED_BYTE, image);
            GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, minFilter);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, magFilter);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, wrapS);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, wrapT);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);

            stbi_image_free(image);
        }

        _isValid = true;
    }

    public Texture2D(String filename, boolean flipImageY){
        this(filename, flipImageY, GL33.GL_LINEAR_MIPMAP_LINEAR,
                GL33.GL_LINEAR, GL33.GL_REPEAT, GL33.GL_REPEAT);
    }

    public Texture2D(ByteBuffer pixels, int width, int height, int nrComp, int minFilter, int magFilter, int wrapS, int wrapT){

        _nrComp = nrComp;
        _width = width;
        _height = height;

        int format = getColorFormat(_nrComp);
        _rendererID = GL33.glGenTextures();
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _rendererID);
        GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, format, _width, _height, 0,
                format, GL33.GL_UNSIGNED_BYTE, pixels);
        GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, minFilter);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, magFilter);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, wrapS);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, wrapT);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);

        _isValid = true;
    }

    public Texture2D(ByteBuffer pixels, int width, int height, int nrComp){
        this(pixels, width, height, nrComp, GL33.GL_LINEAR_MIPMAP_LINEAR,
                GL33.GL_LINEAR, GL33.GL_REPEAT, GL33.GL_REPEAT);
    }

    public String getFilepath(){
        return _filepath;
    }

    public int getWidth(){
        return _width;
    }

    public int getHeight(){
        return _height;
    }

    public int getNrComp(){
        return _nrComp;
    }

    public void bind(int slot){
        _bindSlot = slot;
        bind();
    }

    @Override
    public int getRendererID() {
        return _rendererID;
    }

    @Override
    public void bind() {
        assert _isValid : "Trying to bind destroyed texture";
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + _bindSlot);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _rendererID);
    }

    @Override
    public void unbind() {
        assert _isValid : "Trying to unbind destroyed texture";
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + _bindSlot);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy() {
        GL33.glDeleteTextures(_rendererID);
        _isValid = false;
        System.out.println("Texture2D destroyed");
    }

    private static int getColorFormat(int nrComp){
        switch (nrComp){
            case 1:  return GL33.GL_ALPHA;
            case 3:  return GL33.GL_RGB;
            case 4:  return GL33.GL_RGBA;
            default: return GL33.GL_RED;
        }
    }
}
