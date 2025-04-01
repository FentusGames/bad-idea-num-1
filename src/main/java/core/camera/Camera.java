package core.camera;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Camera {
	private long windowPtr;

	private Vector3f position = new Vector3f();
	private float zoomLevel = 1.0F;
	private Matrix4f projectionMatrix;
	private float extraWidthFactor = 0.0f;
	// Target internal resolution
	private final float TARGET_WIDTH = 1366.0f;
	private final float TARGET_HEIGHT = 768.0f;
	private final float TARGET_ASPECT = TARGET_WIDTH / TARGET_HEIGHT;

	private int viewportX, viewportY, viewportWidth, viewportHeight;
	private float actualViewWidth, actualViewHeight;

	public Camera(long windowPtr) {
		this.windowPtr = windowPtr;
		this.extraWidthFactor = 1.0f;
	}

	public void apply() {
		// Get the actual window size
		int[] windowWidth = new int[1], windowHeight = new int[1];
		GLFW.glfwGetFramebufferSize(windowPtr, windowWidth, windowHeight);

		// Calculate current aspect ratio
		float currentAspect = (float) windowWidth[0] / (float) windowHeight[0];

		// Calculate the viewport size and position for letter boxing
		if (currentAspect > TARGET_ASPECT) {
			// Window is wider than target aspect - letter box on sides or expand view
			viewportHeight = windowHeight[0];
			viewportWidth = (int) (windowHeight[0] * TARGET_ASPECT);
			viewportX = (windowWidth[0] - viewportWidth) / 2;
			viewportY = 0;
		} else {
			// Window is taller than target aspect - letter box on top/bottom
			viewportWidth = windowWidth[0];
			viewportHeight = (int) (windowWidth[0] / TARGET_ASPECT);
			viewportX = 0;
			viewportY = (windowHeight[0] - viewportHeight) / 2;
		}

		// Set the viewport to the letter boxed area
		GL11.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

		// Create projection matrix
		projectionMatrix = new Matrix4f();

		// Calculate how much to expand the view horizontally
		actualViewHeight = TARGET_HEIGHT;
		actualViewWidth = TARGET_WIDTH;

		// If wider than target aspect, expand the horizontal view
		if (currentAspect > TARGET_ASPECT && extraWidthFactor > 0) {
			// Calculate how much extra width is available in the window
			float extraWidth = windowWidth[0] - viewportWidth;

			// Calculate how much additional world space to include based on extraWidthFactor
			float extraWorldWidth = (extraWidth / viewportWidth) * TARGET_WIDTH * extraWidthFactor;

			// Update the actual view width to include extra space
			actualViewWidth = TARGET_WIDTH + extraWorldWidth;

			// Adjust viewport to use full window width
			viewportX = 0;
			viewportWidth = windowWidth[0];

			// Update viewport
			GL11.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
		}

		// For 2D orthographic projection with calculated view dimensions
		float halfWidth = actualViewWidth / 2.0f;
		float halfHeight = actualViewHeight / 2.0f;

		// Create orthographic projection
		projectionMatrix.ortho2D(-halfWidth, halfWidth, -halfHeight, halfHeight);

		// Apply zoom (scaling)
		projectionMatrix.scale(zoomLevel);

		// Apply camera position (translation)
		projectionMatrix.translate(-position.x, -position.y, -position.z);

		// Load the projection matrix
		FloatBuffer projectionViewBuffer = BufferUtils.createFloatBuffer(16);
		projectionMatrix.get(projectionViewBuffer);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrixf(projectionViewBuffer);

		// Clear the entire window to black for letter boxing
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	public Vector3f getMousePosition() {
		double[] xpos = new double[1], ypos = new double[1];
		GLFW.glfwGetCursorPos(windowPtr, xpos, ypos);

		int[] width = new int[1], height = new int[1];
		GLFW.glfwGetFramebufferSize(windowPtr, width, height);

		// Invert the y-axis as screen coordinates start at the top left corner
		float normalizedX = (float) (xpos[0] / width[0] * 2 - 1);
		float normalizedY = (float) ((height[0] - ypos[0]) / height[0] * 2 - 1);

		Matrix4f inverseProjectionMatrix = new Matrix4f();
		inverseProjectionMatrix.set(projectionMatrix).invert();

		Vector4f normalizedCoords = new Vector4f(normalizedX, normalizedY, -1, 1);
		Vector4f eyeCoords = normalizedCoords.mul(inverseProjectionMatrix);

		// Invert the z-coordinate to transform from view space to world space
		eyeCoords.z = -1;
		eyeCoords.w = 0;

		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.translate(position.x, position.y, position.z);

		Matrix4f inverseViewMatrix = new Matrix4f();
		inverseViewMatrix.set(viewMatrix).invert();

		Vector4f worldCoords = eyeCoords.mul(inverseViewMatrix);
		Vector3f mousePos = new Vector3f(worldCoords.x, worldCoords.y, worldCoords.z);

		return new Vector3f(mousePos.x, mousePos.y, 0);
	}

	public Vector3f screenToWorldCoords(float screenX, float screenY) {
		int[] width = new int[1], height = new int[1];
		GLFW.glfwGetFramebufferSize(windowPtr, width, height);

		// Invert the y-axis as screen coordinates start at the top left corner
		float normalizedX = (float) (screenX / width[0] * 2 - 1);
		float normalizedY = (float) ((height[0] - screenY) / height[0] * 2 - 1);

		Matrix4f inverseProjectionMatrix = new Matrix4f();
		inverseProjectionMatrix.set(projectionMatrix).invert();

		Vector4f normalizedCoords = new Vector4f(normalizedX, normalizedY, -1, 1);
		Vector4f eyeCoords = normalizedCoords.mul(inverseProjectionMatrix);

		// Invert the z-coordinate to transform from view space to world space
		eyeCoords.z = -1;
		eyeCoords.w = 0;

		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.translate(position.x, position.y, position.z);

		Matrix4f inverseViewMatrix = new Matrix4f();
		inverseViewMatrix.set(viewMatrix).invert();

		Vector4f worldCoords = eyeCoords.mul(inverseViewMatrix);
		Vector3f mousePos = new Vector3f(worldCoords.x, worldCoords.y, worldCoords.z);

		return new Vector3f(mousePos.x, mousePos.y, 0);
	}

	public boolean isPointInView(Vector3f point) {
		// Get the view matrix
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.translate(position.x, position.y, position.z);

		// Calculate the inverse of the view matrix
		Matrix4f inverseViewMatrix = new Matrix4f();
		inverseViewMatrix.set(viewMatrix).invert();

		// Transform the point into view space
		Vector4f pointView = new Vector4f(point, 1.0f).mul(inverseViewMatrix);
		Vector3f pointView3 = new Vector3f(pointView.x, pointView.y, pointView.z);

		// Calculate the half-width and half-height in view space (accounting for zoom)
		float halfWidth = (actualViewWidth / 2.0f) / zoomLevel;
		float halfHeight = (actualViewHeight / 2.0f) / zoomLevel;

		// Check if the point is within the camera's frustum
		// @formatter:off
	    return	pointView3.x >= -halfWidth && pointView3.x <= halfWidth && 
	    		pointView3.y >= -halfHeight && pointView3.y <= halfHeight && 
	    		pointView3.z >= -1.0f && pointView3.z <= 1.0f;
	   	// @formatter:on
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setZoomLevel(float zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public float getZoomLevel() {
		return zoomLevel;
	}

	public void setExtraWidthFactor(float factor) {
		// Clamp between 0 (no extra width) and 1 (all extra width)
		this.extraWidthFactor = Math.max(0.0f, Math.min(1.0f, factor));
	}

	public float getExtraWidthFactor() {
		return extraWidthFactor;
	}

	public float getActualViewWidth() {
		return actualViewWidth;
	}

	public float getActualViewHeight() {
		return actualViewHeight;
	}

	public Vector2f worldToScreenCoords(Vector3f worldPos) {
		// Create a Vector4f with w component of 1.0f
		Vector4f clipPos = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1.0f);

		// Multiply the clipPos by the projection matrix
		projectionMatrix.transform(clipPos);

		// Divide the x, y, and z components by the w component
		clipPos.x /= clipPos.w;
		clipPos.y /= clipPos.w;
		clipPos.z /= clipPos.w;

		// Map the NDC to screen space
		int[] width = new int[1], height = new int[1];
		GLFW.glfwGetFramebufferSize(windowPtr, width, height);
		float screenX = (clipPos.x + 1.0f) / 2.0f * width[0];
		float screenY = (1.0f - clipPos.y) / 2.0f * height[0];

		// Create a new Vector2f with the screen space coordinates
		return new Vector2f(screenX, screenY);
	}
}