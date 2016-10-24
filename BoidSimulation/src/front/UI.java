package front;

import java.util.ArrayList;

import back.Boid;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UI extends Application {
	int width = 1000, height = 800;
	float x = 100, y = 100;
	double smoothing = 20;

	ArrayList<Boid> boidList = new ArrayList<Boid>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		Boid circle1 = new Boid(300, 400, 20, 0, 0);
		circle1.setFill(Color.AQUA);
		boidList.add(circle1);

		circle1 = new Boid(700, 400, 20, 0, 0);
		circle1.setFill(Color.GOLD);
		boidList.add(circle1);

		circle1 = new Boid(200, y + 20, 20, 0, 0);
		circle1.setFill(Color.BLACK);
		boidList.add(circle1);

		circle1 = new Boid(50, y + 30, 20, 0, 0);
		circle1.setFill(Color.BROWN);
		boidList.add(circle1);

		circle1 = new Boid(156, y + 40, 20, 0, 0);
		circle1.setFill(Color.GRAY);
		boidList.add(circle1);
		
		for (int i = 0; i < 150; i++){
			circle1 = new Boid((float)Math.random()*width, (float)Math.random()*height, 6, 0, 0);
			circle1.setFill(Color.color(Math.random(),Math.random(),Math.random()));
			boidList.add(circle1);
		}

		Group root = new Group(); // deleted circle parameter

		Scene scene = new Scene(root, width, height);

		// Add circles to the group of things
		for (int i = 0; i < boidList.size(); i++) {
			Boid boid = boidList.get(i);
			root.getChildren().add(boid);
		}

		// Setup animation
		KeyFrame frame = new KeyFrame(Duration.seconds(1.0/24.0), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				// --> Draw Boids.
				drawBoids();

				// --> Update Boids' positions.
				updateBoids();

				// // Boid Collision Detection
				// checkCollisions();

				// --> Wall Collision Detection
				for (int i = 0; i < boidList.size(); i++) {
					Boid colorCircle = boidList.get(i);
					wallCollisionDetecter(colorCircle, scene);
				}
			}
		});

		TimelineBuilder.create().cycleCount(javafx.animation.Animation.INDEFINITE).keyFrames(frame).build().play();

		primaryStage.setTitle("Animation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void drawBoids() {
		for (Boid boid : boidList) {
			boid.setTranslateX(boid.getTranslateX() + boid.getDx());
			boid.setTranslateY(boid.getTranslateY() + boid.getDy());
		}
	}

	private void updateBoids() {
		for (Boid boid : boidList) {
			Point2D vector1 = steerToCenterOfFlock(boid);
			Point2D vector2 = collisionAvoidance(boid);
			Point2D vector3 = matchVelocity(boid);
			
			int leoniscorrect = -1 % 4;

//			boid.speed.x = (float) (boid.speed.x + vector1.getX() + vector3.getX() );
//			boid.speed.y = (float) (boid.speed.y + vector1.getY() + vector3.getY() );
			boid.speed.x = (float) (boid.speed.x + vector1.getX() + vector2.getX() + vector3.getX());
			boid.speed.y = (float) (boid.speed.y + vector1.getY() + vector2.getY() + vector3.getY());
			double newX = mod((boid.position.getX()+boid.speed.x) , width);
			double newY = mod((boid.position.getY()+boid.speed.y) , height);

			Point2D newPos = new Point2D(newX,newY);
			boid.setPosition(newPos);
		}
	}
	double mod(double x, double m) {
	    return (x%m + m)%m;
	}

	private Point2D matchVelocity(Boid b) {
		Point2D vector3 = new Point2D(0,0);
		for (Boid boid : boidList) {
			if (!boid.equals(b)) {
				vector3 = vector3.add(boid.speed.x, boid.speed.y);
			}
		}
		double v3x = ((vector3.getX() / (boidList.size() - 1)) - b.speed.x) / smoothing;
		double v3y = ((vector3.getY() / (boidList.size() - 1)) - b.speed.y) / smoothing;
		return new Point2D(v3x, v3y);
	}

	private Point2D collisionAvoidance(Boid b) {
		// Vector c = 0;
		Point2D vector2 = new Point2D(0, 0);
		
		for (Boid boid : boidList) {
			if (!boid.equals(b)) {
				// IF |b.position - bJ.position| < 100 THEN
//				if (Math.abs(boid.position.getX() - b.position.getX()) < ( 100 ) &&
//						Math.abs(boid.position.getY() - b.position.getY()) < ( 100 )	) {
				if(b.collide(boid, 50)){
					
//					vector2 = vector2.subtract(boid.position.subtract(b.position)).multiply(1.0/boid.position.distance(b.position));
					vector2 = vector2.subtract(boid.position.subtract(b.position)).normalize().multiply(2);
				}
			}
		}

		return vector2;
	}

	private Point2D steerToCenterOfFlock(Boid b) {
		Point2D center =  new Point2D(0,0);
		
		for (Boid boid : boidList) {
			if (!boid.equals(b)) {
				Point2D currentBoid = new Point2D(boid.getCenterX() + boid.getTranslateX() + boid.getRadius(),
						boid.getCenterY() + boid.getTranslateY() + boid.getRadius());
				center = center.add(currentBoid);
			}
		}
		Point2D bPosintion = new Point2D(b.getCenterX() + b.getTranslateX() + b.getRadius(),
				b.getCenterY() + b.getTranslateY() + b.getRadius());
		double v3x = ((center.getX() / (boidList.size() - 1)) - bPosintion.getX()) / 100;
		double v3y = ((center.getY() / (boidList.size() - 1)) - bPosintion.getY()) / 100;

		return new Point2D(v3x, v3y);
	}

	private void wallCollisionDetecter(Boid colorCircle, Scene scene) {
		// Top
		if (colorCircle.getCenterX() + colorCircle.getTranslateX() < colorCircle.getRadius()) {
			if (colorCircle.getDx() < 0) {
				colorCircle.setDx(-colorCircle.getDx());
			}
		}
		// Bottom
		else if (colorCircle.getCenterX() + colorCircle.getTranslateX() + colorCircle.getRadius() > scene.getWidth()) {
			if (colorCircle.getDx() > 0) {
				colorCircle.setDx(-colorCircle.getDx());
			}
		}
		// Left
		else if (colorCircle.getCenterY() + colorCircle.getTranslateY() < colorCircle.getRadius()) {
			if (colorCircle.getDy() < 0) {
				colorCircle.setDy(-colorCircle.getDy());
			}
		}
		// Right
		else if (colorCircle.getCenterY() + colorCircle.getTranslateY() + colorCircle.getRadius() > scene.getHeight()) {
			if (colorCircle.getDy() > 0) {
				colorCircle.setDy(-colorCircle.getDy());
			}
		}
	}

	protected void checkCollisions() {
		// Check each boid against any other boid.
		for (Boid boidA : boidList) {
			for (Boid boidB : boidList) {
				if (boidA.collide(boidB)) {
					avoidCollision(boidA, boidB);
				}
			}
		}
		for (Boid boidA : boidList) {
			boidA.collided = false;
		}
	}

	protected boolean avoidCollision(Boid boidA, Boid boidB) {
		if (boidA != boidB) {

			float newVelX1 = (boidA.speed.x * (boidA.mass - boidB.mass) + (2 * boidB.mass * boidB.speed.x))
					/ (boidA.mass + boidB.mass);
			float newVelY1 = (boidA.speed.y * (boidA.mass - boidB.mass) + (2 * boidB.mass * boidB.speed.y))
					/ (boidA.mass + boidB.mass);
			float newVelX2 = (boidB.speed.x * (boidB.mass - boidA.mass) + (2 * boidA.mass * boidA.speed.x))
					/ (boidA.mass + boidB.mass);
			float newVelY2 = (boidB.speed.y * (boidB.mass - boidA.mass) + (2 * boidA.mass * boidA.speed.y))
					/ (boidA.mass + boidB.mass);

			if (!boidA.collided) {
				boidA.setDx(newVelX1);
				boidA.setDy(newVelY1);
				boidA.collided = true;
			}
			
			if (!boidB.collided) {
				boidB.setDx(newVelX2);
				boidB.setDy(newVelY2);
				boidB.collided = true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		Application.launch();
	}
}
