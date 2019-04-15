package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Shader implements Disposable {

    Mesh mesh;
    ShaderProgram shader;
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();

    private final Rectangle scissorBounds = new Rectangle();

    //Position attribute - (x, y)
    public static final int POSITION_COMPONENTS = 2;

    //Color attribute - (r, g, b, a)
    public static final int COLOR_COMPONENTS = 1;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;

    //The maximum number of triangles our mesh will hold
    public static final int MAX_TRIS = 1000;

    //The maximum number of vertices our mesh will hold
    public static final int MAX_VERTS = MAX_TRIS * 3;

    // vertices array
    private float[] vertices = new float[MAX_VERTS * NUM_COMPONENTS];

    // index position
    private int idx = 0;




    public Shader() {
        mesh = new Mesh(true, MAX_VERTS, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        shader = createMeshShader();
    }








    static public ShaderProgram createMeshShader() {
        String VERT_SHADER = "attribute vec2 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                + "uniform mat4 u_projTrans;\n"
                + "varying vec4 vColor;\n"
                + "void main() {\n"
                + "    vColor = "+ ShaderProgram.COLOR_ATTRIBUTE +";\n"
                //+ "    gl_Position =  u_projTrans * "+ ShaderProgram.POSITION_ATTRIBUTE +";\n"
                + "    gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n"
                + "}";

        String FRAG_SHADER =
                "#ifdef GL_ES\n" +
                        "precision mediump float;\n" +
                        "#endif\n" +
                        "varying vec4 vColor;\n" +
                        "void main() {\n" +
                        "	gl_FragColor = vColor;\n" +
                        "}";

        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log!=null && log.length()!=0)
            System.out.println("Shader Log: "+log);
        return shader;
    }


    public void flush() {
        // if we've already flushed
        if (idx == 0) return;

        // sends vertex data to the mesh
        mesh.setVertices(vertices);

        // no need for depth...
        Gdx.gl.glDepthMask(false);
        // enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        // enable scissors
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        // set scissors
        Gdx.gl.glScissor((int)scissorBounds.x, (int)scissorBounds.y, (int)scissorBounds.width, (int)scissorBounds.height);

        // number of vertices we need to render
        int vertexCount = idx / NUM_COMPONENTS;

        shader.begin();

        // update the projection matrix so triangles are rendered in 2D
        setupMatrices();

        // render the mesh
        mesh.render(shader, GL20.GL_TRIANGLES, 0, vertexCount);

        shader.end();

        // re-enable depth to reset states to their default
        Gdx.gl.glDepthMask(true);
        // disable blending
        Gdx.gl.glDisable(GL20.GL_BLEND);
        // disable scissors
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        // reset index to zero
        idx = 0;
    }

    /** Sets one vertex
     *
     * @param x xPos argument
     * @param y yPos argument
     * @param packedColor packed color argument
     */
    public void vertex(float x, float y, float packedColor) {
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = packedColor;
    }


    public void setTransformMatrix(Matrix4 transform) {
        transformMatrix.set(transform);
    }

    public void setProjectionMatrix(Matrix4 projection) {
        projectionMatrix.set(projection);
    }

    private void setupMatrices() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        shader.setUniformMatrix("u_projTrans", combinedMatrix);
    }

    public void setScissorBounds(float x, float y, float width, float height) {
        scissorBounds.set(x, y, width, height);
    }

    public void setScissorBounds(Rectangle scissorBounds) {
        this.scissorBounds.set(scissorBounds);
    }


    @Override
    public void dispose() {
        mesh.dispose();
        shader.dispose();
    }
}
