package glCore.renderer;

import glCore.core.Ref;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IndexBuffer extends Ref implements IRendererResource {

    private final int _rendererID;
    private final int _count;
    private boolean _isValid;

/*    public IndexBuffer(byte[] indices){
        _rendererID = GL33.glGenBuffers();

        // GL_ELEMENT_ARRAY_BUFFER is not valid without an actively bound VAO
        // Binding with GL_ARRAY_BUFFER allows the data to be loaded regardless of VAO state.
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, _rendererID);

        ByteBuffer iBuffer = ByteBuffer.allocateDirect(indices.length);
        iBuffer.put(indices);
        iBuffer.flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, iBuffer.asFloatBuffer(), GL33.GL_STATIC_DRAW);

        _count = indices.length * Integer.BYTES;
        _isValid = true;
    }*/

    public IndexBuffer(int[] indices){
        _rendererID = GL33.glGenBuffers();

        // GL_ELEMENT_ARRAY_BUFFER is not valid without an actively bound VAO
        // Binding with GL_ARRAY_BUFFER allows the data to be loaded regardless of VAO state.
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, _rendererID);
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, indices, GL33.GL_STATIC_DRAW);

        _count = indices.length * Integer.BYTES;
        _isValid = true;
    }

    public int getCount(){
        return _count;
    }

    @Override
    public int getRendererID() {
        return _rendererID;
    }

    @Override
    public void bind(){
        assert _isValid : "Trying to bind destroyed buffer";
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, _rendererID);
    }

    @Override
    public void unbind(){
        assert _isValid : "Trying to unbind destroyed buffer";
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy(){
        GL33.glDeleteBuffers(_rendererID);
        _isValid = false;
        System.out.println("Index Buffer destroyed");
    }
}
