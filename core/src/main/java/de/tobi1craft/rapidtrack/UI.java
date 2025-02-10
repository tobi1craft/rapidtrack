package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.HashMap;

public class UI {
    private static final HashMap<Integer, BitmapFont> fonts = new HashMap<>();

    private UI() {
    }

    public static Skin createSkin() {
        return new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json")); //TODO: Maybe do own UI skin
    }

    public static BitmapFont getFont(int size, Color color) {
        if (fonts.containsKey(size)) return fonts.get(size);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copse.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        fonts.put(size, font);
        generator.dispose();
        return font;
    }

    public static TextButton getTextButton(String text, float height) {
        return getTextButton(text, height, Color.WHITE);
    }

    public static TextButton getTextButton(String text, float height, Color color) {
        TextButton.TextButtonStyle style = ResourceManager.getInstance().getSkin().get(TextButton.TextButtonStyle.class);
        height = Math.max(height, style.up.getMinHeight());
        style.font = getFont((int) (height / 1.618f), color);
        TextButton button = new TextButton(text, style);
        button.setHeight(height);
        return button;
    }
}
