package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import de.tobi1craft.rapidtrack.util.BulletPhysicsSystem;


public class BaseScreen extends ScreenAdapter {

    final float GRID_MIN = -100f;
    final float GRID_MAX = 100f;
    final float GRID_STEP = 10f;
    private final Array<Color> colors;
    private final Stage stage;
    protected PerspectiveCamera camera;
    protected FirstPersonCameraController cameraController;
    protected ModelBatch modelBatch;
    protected ModelBatch shadowBatch;
    protected Array<ModelInstance> renderInstances;
    protected Environment environment;
    protected DirectionalShadowLight shadowLight;
    protected BulletPhysicsSystem bulletPhysicsSystem;
    protected Game game;

    public BaseScreen(Game game) {
        this.game = game;
        bulletPhysicsSystem = new BulletPhysicsSystem();

        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 500;
        camera.position.set(0, 10, 50f);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add((shadowLight = new DirectionalShadowLight(2048, 2048, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -.4f, -.4f, -.4f));
        environment.shadowMap = shadowLight;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
        renderInstances = new Array<>();

        cameraController = new FirstPersonCameraController(camera);
        cameraController.setVelocity(50f);
        cameraController.setDegreesPerPixel(0.2f);
        Gdx.input.setInputProcessor(cameraController);

        colors = new Array<>();
        colors.add(Color.PURPLE);
        colors.add(Color.BLUE);
        colors.add(Color.TEAL);
        colors.add(Color.BROWN);
        colors.add(Color.FIREBRICK);

        createAxes();
    }

    @Override
    public void render(float delta) {
        bulletPhysicsSystem.update(delta);
        cameraController.update(delta);

        ScreenUtils.clear(Color.BLACK, true);

        shadowLight.begin(Vector3.Zero, camera.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(renderInstances);
        shadowBatch.end();
        shadowLight.end();

        modelBatch.begin(camera);
        modelBatch.render(renderInstances, environment);
        modelBatch.end();

        stage.act();
        stage.draw();
    }

    protected void createFloor(float width, float height, float depth) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder = modelBuilder.part("floor", GL20.GL_TRIANGLES, VertexAttribute.Position().usage | VertexAttribute.Normal().usage | VertexAttribute.TexCoords(0).usage, new Material());

        BoxShapeBuilder.build(meshBuilder, width, height, depth);
        btBoxShape btBoxShape = new btBoxShape(new Vector3(width / 2f, height / 2f, depth / 2f));
        Model floor = modelBuilder.end();

        ModelInstance floorInstance = new ModelInstance(floor);
        floorInstance.transform.trn(0, -0.5f, 0f);

        //!                                                                                        0=static object
        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(0, null, btBoxShape, Vector3.Zero);
        btRigidBody body = new btRigidBody(info);

        body.setWorldTransform(floorInstance.transform);

        renderInstances.add(floorInstance);
        bulletPhysicsSystem.addBody(body);
    }

    void createAxes() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        builder = modelBuilder.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, .1f, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, .1f, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, .1f, 0, 0, 0, 100);
        Model axesModel = modelBuilder.end();
        ModelInstance axesInstance = new ModelInstance(axesModel);

        renderInstances.add(axesInstance);
    }

    protected Color getRandomColor() {
        return colors.get(MathUtils.random(0, colors.size - 1));
    }
}
