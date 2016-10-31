package back;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Boid extends Circle {

	float dx = -1.5f;
	float dy = -1.5f;
	public int mass;
	public Speed speed;
	public Boolean collided = false;
	private int delta = 1;
	public Point2D position;

	// Constructor
	public Boid(float x, float y, int radius, float speedX, float speedY) {
		this.setCenterX(x);
		this.setCenterY(y);
		this.setRadius(radius);
		this.mass = radius;
		this.speed = new Speed(speedX, speedY);
		position = new Point2D (x, y);
	}

	// Getters and Setters.
	public float getDx() {
		return speed.x;
	}

	public void setDx(float dx) {
		this.speed.x = dx;
	}

	public float getDy() {
		return speed.y;
	}

	public void setDy(float dy) {
		this.speed.y = dy;
	}
	
	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	/** Helper method - Returns Boolean (Boid collided with other Boid?) */
	public boolean collide(Boid other) {

		Boid otherBoid = other;
		Boid thisBoid = this;

		// Get each boid's center
		Point2D otherCenter = new Point2D(otherBoid.getCenterX() + otherBoid.getTranslateX() + otherBoid.getRadius(),
				otherBoid.getCenterY() + otherBoid.getTranslateY() + otherBoid.getRadius());
		Point2D thisCenter = new Point2D(thisBoid.getCenterX() + thisBoid.getTranslateX() + thisBoid.getRadius(),
				thisBoid.getCenterY() + thisBoid.getTranslateY() + thisBoid.getRadius());
		
		// Calculate distance between boids
		double xDistance = thisCenter.getX() - otherCenter.getX();
		double yDistance = thisCenter.getY() - otherCenter.getY();
	
		double distance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
		
		// Minimum distance to avoid collision
		double minDistance = otherBoid.getRadius() + thisBoid.getRadius() + delta;

		// Compare distance between boids against minimum distance
		return (distance < minDistance);
	}
	
	// --> Overload function to add int to increase minimum distance
	public boolean collide(Boid other, int d) {

		Boid otherBoid = other;
		Boid thisBoid = this;
		
		// Get each boid's center
		Point2D otherCenter = new Point2D(otherBoid.getCenterX() + otherBoid.getTranslateX() + otherBoid.getRadius(),
				otherBoid.getCenterY() + otherBoid.getTranslateY() + otherBoid.getRadius());
		Point2D thisCenter = new Point2D(thisBoid.getCenterX() + thisBoid.getTranslateX() + thisBoid.getRadius(),
				thisBoid.getCenterY() + thisBoid.getTranslateY() + thisBoid.getRadius());
		
		// Calculate distance between boids
		double xDistance = thisCenter.getX() - otherCenter.getX();
		double yDistance = thisCenter.getY() - otherCenter.getY();
		
		double distance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
		
		// Minimum distance to avoid collision + delta
		double minDistance = otherBoid.getRadius() + thisBoid.getRadius() + d;

		// Compare distance between boids against minimum distance
		return (distance < minDistance);
	}

}
