package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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

    public static Label getLiteralLabel(float height, String text) {
        return getLiteralLabel(height, text, Color.WHITE);
    }

    public static Label getLabel(float height, String text, Object... formats) {
        return getLabel(height, Color.WHITE, text, formats);
    }

    public static Label getLabel(float height, Color color, String text, Object... formats) {
        return getLiteralLabel(height, lang.format(text, formats), color);
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

    public static TextField getLiteralTextField(float height, String text) {
        return getLiteralTextField(height, text, Color.WHITE);
    }

    public static TextField getTextField(float height, String text, Object... formats) {
        return getTextField(height, Color.WHITE, text, formats);
    }

    public static TextField getTextField(float height, Color color, String text, Object... formats) {
        return getLiteralTextField(height, lang.format(text, formats), color);
    }

    public static TextField getLiteralTextField(float height, String text, Color color) {
        TextField.TextFieldStyle baseStyle = ResourceManager.getInstance().getSkin().get(TextField.TextFieldStyle.class);
        height = Math.max(height, baseStyle.font.getLineHeight());
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(baseStyle);
        style.font = getFont((int) (height / 1.618f));
        style.fontColor = color;
        TextField field = new TextField(text, style);
        field.setHeight(height);
        return field;
    }
}
