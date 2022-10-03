package glCore.renderer;

import glCore.core.Ref;
import org.lwjgl.opengl.GL33;

public class Framebuffer extends Ref implements IRendererResource {

    static final int _maxFramebufferSize = 8192;

    private int _rendererID;
    private boolean _isValid;
    private int _colorAttachmentRendererID;
    private int _depthAttachmentRendererID;
    private int _width;
    private int _height;
    private int _samples;
    private boolean _hasDepthAtt;


    public Framebuffer(int width, int height, int samples, boolean hasDepthAtt){
        _width = width;
        _height = height;
        _samples = samples;
        _hasDepthAtt = hasDepthAtt;

        _rendererID = 0;

        invalidate();

        _isValid = true;
    }

    public int getWidth(){
        return _width;
    }

    public int getHeight(){
        return _height;
    }

    public int getColorAttachmentRendererID(){
        return  _colorAttachmentRendererID;
    }

    public int getDepthAttachmentRendererID(){
        return _depthAttachmentRendererID;
    }

    public void resize(int width, int height){
        assert width > 0 && height > 0 && width <= _maxFramebufferSize && height <= _maxFramebufferSize :
                "Trying resize framebuffer to: " + width + ", " + height;

        _width = width;
        _height = height;

        invalidate();
    }

    public void blit(Framebuffer src, int mask, int filter){
        blit(this, src, mask, filter);
    }

    @Override
    public int getRendererID() {
        return _rendererID;
    }

    @Override
    public void bind() {
        assert _isValid : "Trying to bind destroyed framebuffer";
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, _rendererID);
    }

    @Override
    public void unbind() {
        assert _isValid : "Trying to unbind destroyed framebuffer";
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy() {
        GL33.glDeleteFramebuffers(_rendererID);
        GL33.glDeleteTextures(_colorAttachmentRendererID);
        if(_hasDepthAtt)
            GL33.glDeleteTextures(_depthAttachmentRendererID);
        _isValid = false;
        System.out.println("Framebuffer destroyed");
    }

    private void invalidate(){
        if(_rendererID > 0){
            GL33.glDeleteFramebuffers(_rendererID);
            GL33.glDeleteTextures(_colorAttachmentRendererID);
            if(_hasDepthAtt)
                GL33.glDeleteTextures(_depthAttachmentRendererID);

            _rendererID = 0;
            _colorAttachmentRendererID = 0;
            _depthAttachmentRendererID = 0;
        }

        _rendererID = GL33.glGenFramebuffers();
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, _rendererID);

        // color attachment texture
        _colorAttachmentRendererID = GL33.glGenTextures();

        if(_samples > 1){
            GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, _colorAttachmentRendererID);
            GL33.glTexImage2DMultisample(GL33.GL_TEXTURE_2D_MULTISAMPLE, _samples,
                    GL33.GL_RGBA8, _width, _height, true);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, 0);

            // attach color tex ms to framebuffer
            GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0,
                    GL33.GL_TEXTURE_2D_MULTISAMPLE, _colorAttachmentRendererID, 0);

        } else{
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, _colorAttachmentRendererID);
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8,
                    _width, _height, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, 0);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);

            // attach color tex to framebuffer
            GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0,
                    GL33.GL_TEXTURE_2D, _colorAttachmentRendererID, 0);
        }

        // depth attachment texture
        if(_samples > 1 && _hasDepthAtt){
            _depthAttachmentRendererID = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, _depthAttachmentRendererID);
            GL33.glTexImage2DMultisample(GL33.GL_TEXTURE_2D_MULTISAMPLE, _samples,
                    GL33.GL_DEPTH24_STENCIL8, _width, _height, true);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, 0);

            // attach depth ms tex to framebuffer
            GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_STENCIL_ATTACHMENT,
                    GL33.GL_TEXTURE_2D_MULTISAMPLE, _depthAttachmentRendererID, 0);

        } else if(_hasDepthAtt){
            _depthAttachmentRendererID = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, _depthAttachmentRendererID);
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_DEPTH24_STENCIL8,
                    _width, _height, 0, GL33.GL_DEPTH_STENCIL, GL33.GL_UNSIGNED_INT_24_8, 0);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);

            // attach depth tex to framebuffer
            GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_STENCIL_ATTACHMENT,
                    GL33.GL_TEXTURE_2D, _depthAttachmentRendererID, 0);
        }

        assert GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) == GL33.GL_FRAMEBUFFER_COMPLETE
                : "Framebuffer is incomplete!";

        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }

    public static void blit(Framebuffer dest, Framebuffer src, int mask, int filter){
        GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, src._rendererID);
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, dest._rendererID);
        GL33.glBlitFramebuffer(0, 0, src._width, src._height,
                0, 0, dest._width, dest._height, mask, filter);

        GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, 0);
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, 0);
    }
}