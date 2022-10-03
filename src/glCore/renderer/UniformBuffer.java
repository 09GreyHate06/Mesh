package glCore.renderer;

import glCore.core.Ref;
import org.lwjgl.opengl.GL33;

import java.nio.ByteBuffer;

public class UniformBuffer extends Ref implements IRendererResource {

    private final int _rendererID;
    private boolean _isValid;
    private final int _binding;

    public UniformBuffer(int binding, int size, int offset){
        _rendererID = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, _rendererID);
        GL33.glBufferData(GL33.GL_UNIFORM_BUFFER, size, GL33.GL_DYNAMIC_DRAW);
        GL33.glBindBufferRange(GL33.GL_UNIFORM_BUFFER, binding, _rendererID, offset, size);
        _binding = binding;
        _isValid = true;
    }

    public int getBindingIndex(){
        return _binding;
    }

    public void setData(int offset, ByteBuffer data){
        bind();
        GL33.glBufferSubData(GL33.GL_UNIFORM_BUFFER, offset, data);
    }

    @Override
    public int getRendererID() {
        return _rendererID;
    }

    @Override
    public void bind() {
        assert _isValid : "Trying to bind destroyed Uniform Buffer";
        GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, _rendererID);
    }

    @Override
    public void unbind() {
        assert _isValid : "Trying to unbind destroyed Uniform Buffer";
        GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy() {
        GL33.glDeleteBuffers(_rendererID);
        _isValid = false;
        System.out.println("Uniform buffer destroyed");
    }
}
