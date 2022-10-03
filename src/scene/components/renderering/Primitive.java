package scene.components.renderering;

import org.lwjgl.opengl.GL33;

public enum Primitive {
    Points(GL33.GL_POINTS),
    Lines(GL33.GL_LINES),
    LineStrip(GL33.GL_LINE_STRIP),
    LineLoop(GL33.GL_LINE_LOOP),
    Triangles(GL33.GL_TRIANGLES),
    TriangleStrip(GL33.GL_TRIANGLE_STRIP),
    TriangleFan(GL33.GL_TRIANGLE_FAN),
    Quads(GL33.GL_QUADS),
    QuadStrip(GL33.GL_QUAD_STRIP);

    public final int nativeValue;

    private Primitive(int nativeValue) {
        this.nativeValue = nativeValue;
    }
}
