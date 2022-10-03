package glCore.renderer;

import glCore.core.Ref;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VertexBuffer extends Ref implements IRendererResource{

    private VertexBufferLayout _layout;
    private final int _rendererID;
    private boolean _isValid;

/*    public VertexBuffer(byte[] vertices, VertexBufferLayout layout){
        _rendererID = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, _rendererID);

        ByteBuffer vBuffer = ByteBuffer.allocateDirect(vertices.length);
        vBuffer.put(vertices);
        vBuffer.flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vBuffer.asFloatBuffer(), GL33.GL_STATIC_DRAW);

        _layout = layout;
        _isValid = true;
    }*/

    public VertexBuffer(float[] vertices, VertexBufferLayout layout){
        _rendererID = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, _rendererID);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
        _layout = layout;
        _isValid = true;
    }

    public VertexBuffer(float[] vertices){
        this(vertices, null);
    }

    public void setLayout(VertexBufferLayout layout){
        _layout = layout;
    }

    public final VertexBufferLayout getLayout(){
        return _layout;
    }

    @Override
    public int getRendererID() {
        return 0;
    }

    @Override
    public void bind(){
        assert _isValid : "Trying to bind destroyed buffer";
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, _rendererID);
    }

    @Override
    public void unbind(){
        assert _isValid : "Trying to unbind destroyed buffer";
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy(){
        GL33.glDeleteBuffers(_rendererID);
        _isValid = false;
        System.out.println("Vertex Buffer destroyed");
    }
}
