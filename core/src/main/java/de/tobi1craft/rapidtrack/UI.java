package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;

public class UI {
    private static final HashMap<Integer, BitmapFont> fonts = new HashMap<>();
    public static I18NBundle lang;

    private UI() {
    }

    public static Skin createSkin() {
        return new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json")); //TODO: Maybe do own UI skin
    }

    public static BitmapFont getFont(int size) {
        if (fonts.containsKey(size)) return fonts.get(size);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copse.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        fonts.put(size, font);
        generator.dispose();
        return font;
    }

    public static TextButton getLiteralTextButton(float height, String text) {
        return getLiteralTextButton(height, text, Color.WHITE);
    }

    public static TextButton getTextButton(float height, String text, Object... formats) {
        return getTextButton(height, Color.WHITE, text, formats);
    }

    public static TextButton getTextButton(float height, Color color, String text, Object... formats) {
        return getLiteralTextButton(height, lang.format(text, formats), color);
    }

    public static TextButton getLiteralTextButton(float height, String text, Color color) {
        TextButton.TextButtonStyle baseStyle = ResourceManager.getInstance().getSkin().get(TextButton.TextButtonStyle.class);
        height = Math.max(height, baseStyle.up.getMinHeight());
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(baseStyle);
        style.font = getFont((int) (height / 1.618f));
        style.fontColor = color;
        TextButton button = new TextButton(text, style);
        button.setHeight(height);
        return button;
    }

    public static Label getLiteralLabel(float height, String text, Color color) {
        Label.LabelStyle baseStyle = ResourceManager.getInstance().getSkin().get(Label.LabelStyle.class);
        height = Math.max(height, baseStyle.font.getLineHeight());
        Label.LabelStyle style = new Label.LabelStyle(baseStyle);
        style.font = getFont((int) (height / 1.618f));
        style.fontColor = color;
        Label label = new Label(text, style);
        label.setHeight(height);
        return label;
    }
}
