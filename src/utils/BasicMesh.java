package utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class BasicMesh {
    static class TangentSpace{
        public Vector3f tangent;
        public Vector3f bitangent;

        public TangentSpace(Vector3f tangent, Vector3f bitangent){
            this.tangent = tangent;
            this.bitangent = bitangent;
        }
    }

    public static float[] createCubeVertices(float minPos, float maxPos, float texCoordS, float texCoordT){
        Vector2f stA = new Vector2f(0.0f, 0.0f);
        Vector2f stB = new Vector2f(texCoordS, 0.0f);
        Vector2f stC = new Vector2f(texCoordS, texCoordT);
        Vector2f stD = new Vector2f(0.0f, texCoordT);

        Vector3f backNormal = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f frontNormal = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f bottomNormal = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f topNormal = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f leftNormal = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f rightNormal = new Vector3f(1.0f, 0.0f, 0.0f);

        // back
        Vector3f backPosA = new Vector3f(maxPos, minPos, minPos);
        Vector3f backPosB = new Vector3f(minPos, minPos, minPos);
        Vector3f backPosC = new Vector3f(minPos, maxPos, minPos);
        Vector3f backPosD = new Vector3f(maxPos, maxPos, minPos);

        TangentSpace backTSA = getTangentSpace(backPosA, backPosB, backPosC, stA, stB, stC);
        TangentSpace backTSB = getTangentSpace(backPosA, backPosC, backPosD, stA, stC, stD);

        // front
        Vector3f frontPosA = new Vector3f(minPos, minPos, maxPos);
        Vector3f frontPosB = new Vector3f(maxPos, minPos, maxPos);
        Vector3f frontPosC = new Vector3f(maxPos, maxPos, maxPos);
        Vector3f frontPosD = new Vector3f(minPos, maxPos, maxPos);

        TangentSpace frontTSA = getTangentSpace(frontPosA, frontPosB, frontPosC, stA, stB, stC);
        TangentSpace frontTSB = getTangentSpace(frontPosA, frontPosC, frontPosD, stA, stC, stD);

        // bottom
        Vector3f bottomPosA = new Vector3f(minPos, minPos, minPos);
        Vector3f bottomPosB = new Vector3f(maxPos, minPos, minPos);
        Vector3f bottomPosC = new Vector3f(maxPos, minPos, maxPos);
        Vector3f bottomPosD = new Vector3f(minPos, minPos, maxPos);

        TangentSpace bottomTSA = getTangentSpace(bottomPosA, bottomPosB, bottomPosC, stA, stB, stC);
        TangentSpace bottomTSB = getTangentSpace(bottomPosA, bottomPosC, bottomPosD, stA, stC, stD);

        // top
        Vector3f topPosA = new Vector3f(minPos, maxPos, maxPos);
        Vector3f topPosB = new Vector3f(maxPos, maxPos, maxPos);
        Vector3f topPosC = new Vector3f(maxPos, maxPos, minPos);
        Vector3f topPosD = new Vector3f(minPos, maxPos, minPos);

        TangentSpace topTSA = getTangentSpace(topPosA, topPosB, topPosC, stA, stB, stC);
        TangentSpace topTSB = getTangentSpace(topPosA, topPosC, topPosD, stA, stC, stD);

        // left
        Vector3f leftPosA = new Vector3f(minPos, minPos, minPos);
        Vector3f leftPosB = new Vector3f(minPos, minPos, maxPos);
        Vector3f leftPosC = new Vector3f(minPos, maxPos, maxPos);
        Vector3f leftPosD = new Vector3f(minPos, maxPos, minPos);

        TangentSpace leftTSA = getTangentSpace(leftPosA, leftPosB, leftPosC, stA, stB, stC);
        TangentSpace leftTSB = getTangentSpace(leftPosA, leftPosC, leftPosD, stA, stC, stD);

        // right
        Vector3f rightPosA = new Vector3f(maxPos, minPos, maxPos);
        Vector3f rightPosB = new Vector3f(maxPos, minPos, minPos);
        Vector3f rightPosC = new Vector3f(maxPos, maxPos, minPos);
        Vector3f rightPosD = new Vector3f(maxPos, maxPos, maxPos);

        TangentSpace rightTSA = getTangentSpace(rightPosA, rightPosB, rightPosC, stA, stB, stC);
        TangentSpace rightTSB = getTangentSpace(rightPosA, rightPosC, rightPosD, stA, stC, stD);

        return new float[]{
                // pos                               // texCoord    //normal                                    // tangent                                                  // bitangent
                
                // back
                backPosA.x, backPosA.y, backPosA.z,  stA.x, stA.y,  backNormal.x, backNormal.y, backNormal.z,   backTSA.tangent.x, backTSA.tangent.y, backTSA.tangent.z,    backTSA.bitangent.x, backTSA.bitangent.y, backTSA.bitangent.z,
                backPosB.x, backPosB.y, backPosB.z,  stB.x, stB.y,  backNormal.x, backNormal.y, backNormal.z,   backTSA.tangent.x, backTSA.tangent.y, backTSA.tangent.z,    backTSA.bitangent.x, backTSA.bitangent.y, backTSA.bitangent.z,
                backPosC.x, backPosC.y, backPosC.z,  stC.x, stC.y,  backNormal.x, backNormal.y, backNormal.z,   backTSA.tangent.x, backTSA.tangent.y, backTSA.tangent.z,    backTSA.bitangent.x, backTSA.bitangent.y, backTSA.bitangent.z,
                backPosC.x, backPosC.y, backPosC.z,  stC.x, stC.y,  backNormal.x, backNormal.y, backNormal.z,   backTSB.tangent.x, backTSB.tangent.y, backTSB.tangent.z,    backTSB.bitangent.x, backTSB.bitangent.y, backTSB.bitangent.z,
                backPosD.x, backPosD.y, backPosD.z,  stD.x, stD.y,  backNormal.x, backNormal.y, backNormal.z,   backTSB.tangent.x, backTSB.tangent.y, backTSB.tangent.z,    backTSB.bitangent.x, backTSB.bitangent.y, backTSB.bitangent.z,
                backPosA.x, backPosA.y, backPosA.z,  stA.x, stA.y,  backNormal.x, backNormal.y, backNormal.z,   backTSB.tangent.x, backTSB.tangent.y, backTSB.tangent.z,    backTSB.bitangent.x, backTSB.bitangent.y, backTSB.bitangent.z,


                // front                                                                                                                                                                                                                 
                frontPosA.x, frontPosA.y, frontPosA.z,  stA.x, stA.y,  frontNormal.x, frontNormal.y, frontNormal.z,   frontTSA.tangent.x, frontTSA.tangent.y, frontTSA.tangent.z,    frontTSA.bitangent.x, frontTSA.bitangent.y, frontTSA.bitangent.z,
                frontPosB.x, frontPosB.y, frontPosB.z,  stB.x, stB.y,  frontNormal.x, frontNormal.y, frontNormal.z,   frontTSA.tangent.x, frontTSA.tangent.y, frontTSA.tangent.z,    frontTSA.bitangent.x, frontTSA.bitangent.y, frontTSA.bitangent.z,
                frontPosC.x, frontPosC.y, frontPosC.z,  stC.x, stC.y,  frontNormal.x, frontNormal.y, frontNormal.z,   frontTSA.tangent.x, frontTSA.tangent.y, frontTSA.tangent.z,    frontTSA.bitangent.x, frontTSA.bitangent.y, frontTSA.bitangent.z,
                frontPosC.x, frontPosC.y, frontPosC.z,  stC.x, stC.y,  frontNormal.x, frontNormal.y, frontNormal.z,   frontTSB.tangent.x, frontTSB.tangent.y, frontTSB.tangent.z,    frontTSB.bitangent.x, frontTSB.bitangent.y, frontTSB.bitangent.z,
                frontPosD.x, frontPosD.y, frontPosD.z,  stD.x, stD.y,  frontNormal.x, frontNormal.y, frontNormal.z,   frontTSB.tangent.x, frontTSB.tangent.y, frontTSB.tangent.z,    frontTSB.bitangent.x, frontTSB.bitangent.y, frontTSB.bitangent.z,
                frontPosA.x, frontPosA.y, frontPosA.z,  stA.x, stA.y,  frontNormal.x, frontNormal.y, frontNormal.z,   frontTSB.tangent.x, frontTSB.tangent.y, frontTSB.tangent.z,    frontTSB.bitangent.x, frontTSB.bitangent.y, frontTSB.bitangent.z,

                // bottom
                bottomPosA.x, bottomPosA.y, bottomPosA.z,  stA.x, stA.y,  bottomNormal.x, bottomNormal.y, bottomNormal.z,   bottomTSA.tangent.x, bottomTSA.tangent.y, bottomTSA.tangent.z,    bottomTSA.bitangent.x, bottomTSA.bitangent.y, bottomTSA.bitangent.z,
                bottomPosB.x, bottomPosB.y, bottomPosB.z,  stB.x, stB.y,  bottomNormal.x, bottomNormal.y, bottomNormal.z,   bottomTSA.tangent.x, bottomTSA.tangent.y, bottomTSA.tangent.z,    bottomTSA.bitangent.x, bottomTSA.bitangent.y, bottomTSA.bitangent.z,
                bottomPosC.x, bottomPosC.y, bottomPosC.z,  stC.x, stC.y,  bottomNormal.x, bottomNormal.y, bottomNormal.z,   bottomTSA.tangent.x, bottomTSA.tangent.y, bottomTSA.tangent.z,    bottomTSA.bitangent.x, bottomTSA.bitangent.y, bottomTSA.bitangent.z,
                bottomPosC.x, bottomPosC.y, bottomPosC.z,  stC.x, stC.y,  bottomNormal.x, bottomNormal.y, bottomNormal.z,   bottomTSB.tangent.x, bottomTSB.tangent.y, bottomTSB.tangent.z,    bottomTSB.bitangent.x, bottomTSB.bitangent.y, bottomTSB.bitangent.z,
                bottomPosD.x, bottomPosD.y, bottomPosD.z,  stD.x, stD.y,  bottomNormal.x, bottomNormal.y, bottomNormal.z,   bottomTSB.tangent.x, bottomTSB.tangent.y, bottomTSB.tangent.z,    bottomTSB.bitangent.x, bottomTSB.bitangent.y, bottomTSB.bitangent.z,
                bottomPosA.x, bottomPosA.y, bottomPosA.z,  stA.x, stA.y,  bottomNormal.x, bottomNormal.y, bottomNormal.z,   bottomTSB.tangent.x, bottomTSB.tangent.y, bottomTSB.tangent.z,    bottomTSB.bitangent.x, bottomTSB.bitangent.y, bottomTSB.bitangent.z,
                
                
                // top
                topPosA.x, topPosA.y, topPosA.z,  stA.x, stA.y,  topNormal.x, topNormal.y, topNormal.z,   topTSA.tangent.x, topTSA.tangent.y, topTSA.tangent.z,    topTSA.bitangent.x, topTSA.bitangent.y, topTSA.bitangent.z,
                topPosB.x, topPosB.y, topPosB.z,  stB.x, stB.y,  topNormal.x, topNormal.y, topNormal.z,   topTSA.tangent.x, topTSA.tangent.y, topTSA.tangent.z,    topTSA.bitangent.x, topTSA.bitangent.y, topTSA.bitangent.z,
                topPosC.x, topPosC.y, topPosC.z,  stC.x, stC.y,  topNormal.x, topNormal.y, topNormal.z,   topTSA.tangent.x, topTSA.tangent.y, topTSA.tangent.z,    topTSA.bitangent.x, topTSA.bitangent.y, topTSA.bitangent.z,
                topPosC.x, topPosC.y, topPosC.z,  stC.x, stC.y,  topNormal.x, topNormal.y, topNormal.z,   topTSB.tangent.x, topTSB.tangent.y, topTSB.tangent.z,    topTSB.bitangent.x, topTSB.bitangent.y, topTSB.bitangent.z,
                topPosD.x, topPosD.y, topPosD.z,  stD.x, stD.y,  topNormal.x, topNormal.y, topNormal.z,   topTSB.tangent.x, topTSB.tangent.y, topTSB.tangent.z,    topTSB.bitangent.x, topTSB.bitangent.y, topTSB.bitangent.z,
                topPosA.x, topPosA.y, topPosA.z,  stA.x, stA.y,  topNormal.x, topNormal.y, topNormal.z,   topTSB.tangent.x, topTSB.tangent.y, topTSB.tangent.z,    topTSB.bitangent.x, topTSB.bitangent.y, topTSB.bitangent.z,
                
                
                // left
                leftPosA.x, leftPosA.y, leftPosA.z,  stA.x, stA.y,  leftNormal.x, leftNormal.y, leftNormal.z,   leftTSA.tangent.x, leftTSA.tangent.y, leftTSA.tangent.z,    leftTSA.bitangent.x, leftTSA.bitangent.y, leftTSA.bitangent.z,
                leftPosB.x, leftPosB.y, leftPosB.z,  stB.x, stB.y,  leftNormal.x, leftNormal.y, leftNormal.z,   leftTSA.tangent.x, leftTSA.tangent.y, leftTSA.tangent.z,    leftTSA.bitangent.x, leftTSA.bitangent.y, leftTSA.bitangent.z,
                leftPosC.x, leftPosC.y, leftPosC.z,  stC.x, stC.y,  leftNormal.x, leftNormal.y, leftNormal.z,   leftTSA.tangent.x, leftTSA.tangent.y, leftTSA.tangent.z,    leftTSA.bitangent.x, leftTSA.bitangent.y, leftTSA.bitangent.z,
                leftPosC.x, leftPosC.y, leftPosC.z,  stC.x, stC.y,  leftNormal.x, leftNormal.y, leftNormal.z,   leftTSB.tangent.x, leftTSB.tangent.y, leftTSB.tangent.z,    leftTSB.bitangent.x, leftTSB.bitangent.y, leftTSB.bitangent.z,
                leftPosD.x, leftPosD.y, leftPosD.z,  stD.x, stD.y,  leftNormal.x, leftNormal.y, leftNormal.z,   leftTSB.tangent.x, leftTSB.tangent.y, leftTSB.tangent.z,    leftTSB.bitangent.x, leftTSB.bitangent.y, leftTSB.bitangent.z,
                leftPosA.x, leftPosA.y, leftPosA.z,  stA.x, stA.y,  leftNormal.x, leftNormal.y, leftNormal.z,   leftTSB.tangent.x, leftTSB.tangent.y, leftTSB.tangent.z,    leftTSB.bitangent.x, leftTSB.bitangent.y, leftTSB.bitangent.z,
                
                
                // right
                rightPosA.x, rightPosA.y, rightPosA.z,  stA.x, stA.y,  rightNormal.x, rightNormal.y, rightNormal.z,   rightTSA.tangent.x, rightTSA.tangent.y, rightTSA.tangent.z,    rightTSA.bitangent.x, rightTSA.bitangent.y, rightTSA.bitangent.z,
                rightPosB.x, rightPosB.y, rightPosB.z,  stB.x, stB.y,  rightNormal.x, rightNormal.y, rightNormal.z,   rightTSA.tangent.x, rightTSA.tangent.y, rightTSA.tangent.z,    rightTSA.bitangent.x, rightTSA.bitangent.y, rightTSA.bitangent.z,
                rightPosC.x, rightPosC.y, rightPosC.z,  stC.x, stC.y,  rightNormal.x, rightNormal.y, rightNormal.z,   rightTSA.tangent.x, rightTSA.tangent.y, rightTSA.tangent.z,    rightTSA.bitangent.x, rightTSA.bitangent.y, rightTSA.bitangent.z,
                rightPosC.x, rightPosC.y, rightPosC.z,  stC.x, stC.y,  rightNormal.x, rightNormal.y, rightNormal.z,   rightTSB.tangent.x, rightTSB.tangent.y, rightTSB.tangent.z,    rightTSB.bitangent.x, rightTSB.bitangent.y, rightTSB.bitangent.z,
                rightPosD.x, rightPosD.y, rightPosD.z,  stD.x, stD.y,  rightNormal.x, rightNormal.y, rightNormal.z,   rightTSB.tangent.x, rightTSB.tangent.y, rightTSB.tangent.z,    rightTSB.bitangent.x, rightTSB.bitangent.y, rightTSB.bitangent.z,
                rightPosA.x, rightPosA.y, rightPosA.z,  stA.x, stA.y,  rightNormal.x, rightNormal.y, rightNormal.z,   rightTSB.tangent.x, rightTSB.tangent.y, rightTSB.tangent.z,    rightTSB.bitangent.x, rightTSB.bitangent.y, rightTSB.bitangent.z,
        };
    }

    public static float[] createPlaneVertices(float minPos, float maxPos, float texCoordS, float texCoordT){
        Vector2f stA = new Vector2f(0.0f, 0.0f);
        Vector2f stB = new Vector2f(texCoordS, 0.0f);
        Vector2f stC = new Vector2f(texCoordS, texCoordT);
        Vector2f stD = new Vector2f(0.0f, texCoordT);

        Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);

        Vector3f posA = new Vector3f(minPos, 0.0f, maxPos);
        Vector3f posB = new Vector3f(maxPos, 0.0f, maxPos);
        Vector3f posC = new Vector3f(maxPos, 0.0f, minPos);
        Vector3f posD = new Vector3f(minPos, 0.0f, minPos);

        TangentSpace tsA = getTangentSpace(posA, posB,posC, stA,stB, stC);
        TangentSpace tsB = getTangentSpace(posA, posC,posD, stA,stC, stD);

        return new float[]{
                posA.x, posA.y, posA.z,  stA.x, stA.y,  normal.x, normal.y, normal.z,   tsA.tangent.x, tsA.tangent.y, tsA.tangent.z,    tsA.bitangent.x, tsA.bitangent.y, tsA.bitangent.z,
                posB.x, posB.y, posB.z,  stB.x, stB.y,  normal.x, normal.y, normal.z,   tsA.tangent.x, tsA.tangent.y, tsA.tangent.z,    tsA.bitangent.x, tsA.bitangent.y, tsA.bitangent.z,
                posC.x, posC.y, posC.z,  stC.x, stC.y,  normal.x, normal.y, normal.z,   tsA.tangent.x, tsA.tangent.y, tsA.tangent.z,    tsA.bitangent.x, tsA.bitangent.y, tsA.bitangent.z,
                posC.x, posC.y, posC.z,  stC.x, stC.y,  normal.x, normal.y, normal.z,   tsB.tangent.x, tsB.tangent.y, tsB.tangent.z,    tsB.bitangent.x, tsB.bitangent.y, tsB.bitangent.z,
                posD.x, posD.y, posD.z,  stD.x, stD.y,  normal.x, normal.y, normal.z,   tsB.tangent.x, tsB.tangent.y, tsB.tangent.z,    tsB.bitangent.x, tsB.bitangent.y, tsB.bitangent.z,
                posA.x, posA.y, posA.z,  stA.x, stA.y,  normal.x, normal.y, normal.z,   tsB.tangent.x, tsB.tangent.y, tsB.tangent.z,    tsB.bitangent.x, tsB.bitangent.y, tsB.bitangent.z,
        };
    }

    public static int[] createCubeIndices(){
        int[] indices = new int[36];
        for(int i = 0; i < indices.length; i++)
            indices[i] = i;

        return indices;
    }

    public static int[] createPlaneIndices(){
        int[] indices = new int[6];
        for(int i = 0; i < indices.length; i++)
            indices[i] = i;

        return  indices;
    }

    private static TangentSpace getTangentSpace(final Vector3f posA, final Vector3f posB, final Vector3f posC,
                                         final Vector2f stA, final Vector2f stB, final Vector2f stC){

        Vector3f edgeA = posB.sub(posA, new Vector3f());
        Vector3f edgeB = posC.sub(posA, new Vector3f());
        Vector2f deltaSTA = stB.sub(stA, new Vector2f());
        Vector2f deltaSTB = stC.sub(stA, new Vector2f());

        float f = 1.0f / (deltaSTA.x * deltaSTB.y - deltaSTB.x * deltaSTA.y);

        Vector3f tangent = new Vector3f();
        tangent.x = f * (deltaSTB.y * edgeA.x - deltaSTA.y * edgeB.x);
        tangent.y = f * (deltaSTB.y * edgeA.y - deltaSTA.y * edgeB.y);
        tangent.z = f * (deltaSTB.y * edgeA.z - deltaSTA.y * edgeB.z);

        Vector3f bitangent = new Vector3f();
        bitangent.x = f * (-deltaSTB.x * edgeA.x + deltaSTA.x * edgeB.x);
        bitangent.y = f * (-deltaSTB.x * edgeA.y + deltaSTA.x * edgeB.y);
        bitangent.z = f * (-deltaSTB.x * edgeA.z + deltaSTA.x * edgeB.z);

        return new TangentSpace(tangent, bitangent);
    }
}
