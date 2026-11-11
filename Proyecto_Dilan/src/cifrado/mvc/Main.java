package cifrado.mvc;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        UIManager.put("Button.arc", 16);
        UIManager.put("Component.arc", 13);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Button.innerFocusWidth", 2);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 10);

        FlatLightLaf.setup();
        CifradoView.configurarTemaInicial();

        SwingUtilities.invokeLater(() -> {
            CifradoStrategy estrategia = new CifradoAfinStrategy();
            CifradoModel model = new CifradoModel(estrategia);
            CifradoView view = new CifradoView();
            new CifradoController(model, view);
            view.setVisible(true);
        });
    }
}
