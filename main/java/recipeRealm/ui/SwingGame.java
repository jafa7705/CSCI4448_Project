package recipeRealm.ui;

import recipeRealm.GameManager;
import recipeRealm.decorator.*;
import recipeRealm.factory.CustomerOrderFactory;
import recipeRealm.model.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * SwingGame — Swing desktop UI for Recipe Realm.
 *
 * Follows the same structural conventions as the Wordle SwingWordle:
 *   - JFrame with BorderLayout
 *   - A central grid/panel area for the main content
 *   - A south panel for controls and messages
 *   - All UI updates on the Event Dispatch Thread via SwingUtilities.invokeLater
 *
 * Run with:
 *   java -cp out recipeRealm.ui.SwingGame
 */
public class SwingGame {

    // ------------------------------------------------------------------ //
    // Colour palette (mirrors Wordle's clean flat style)                  //
    // ------------------------------------------------------------------ //
    static final Color BG             = new Color(245, 245, 240);
    static final Color PANEL_BG       = Color.WHITE;
    static final Color BORDER_LIGHT   = new Color(210, 210, 210);
    static final Color BORDER_MED     = new Color(160, 160, 160);
    static final Color GREEN          = new Color(106, 170, 100);
    static final Color AMBER          = new Color(201, 180, 88);
    static final Color BLUE           = new Color(100, 149, 210);
    static final Color GRAY_DARK      = new Color(120, 124, 126);
    static final Color TEXT_ON_COLOR  = new Color(245, 245, 245);
    static final Color ACCENT         = new Color(80, 80, 80);

    // ------------------------------------------------------------------ //
    // Game state                                                          //
    // ------------------------------------------------------------------ //
    private final GameManager game = new GameManager(1);
    private CustomerOrder pendingOrder = null;
    private int ordersCompleted = 0;
    private static final int[] XP_THRESHOLDS = { 0, 3, 7, 12, 18, 25, 33, 42, 52, 63 };

    // ------------------------------------------------------------------ //
    // Swing components                                                    //
    // ------------------------------------------------------------------ //
    private final JFrame frame = new JFrame("Recipe Realm");

    // Status bar (top)
    private final JLabel lblSkill    = makeLabel("Skill: Lv.1", Font.BOLD, 13);
    private final JLabel lblEarnings = makeLabel("$0.00", Font.PLAIN, 13);
    private final JLabel lblSat      = makeLabel("Satisfaction: —", Font.PLAIN, 13);
    private final JLabel lblXp       = makeLabel("XP: 0 / 3", Font.PLAIN, 13);

    // Order panel (centre)
    private final JLabel lblOrderTitle  = makeLabel("Welcome to Recipe Realm!", Font.BOLD, 18);
    private final JLabel lblRecipeName  = makeLabel("", Font.BOLD, 15);
    private final JLabel lblMethod      = makeLabel("", Font.PLAIN, 13);
    private final JLabel lblSkillReq    = makeLabel("", Font.PLAIN, 13);
    private final JLabel lblPrice       = makeLabel("", Font.PLAIN, 13);
    private final JLabel lblDescription = makeLabel("", Font.ITALIC, 13);

    // Result panel
    private final JLabel lblScore    = makeLabel("", Font.BOLD, 28);
    private final JLabel lblStars    = makeLabel("", Font.PLAIN, 20);
    private final JLabel lblFeedback = makeLabel("", Font.ITALIC, 13);
    private final JLabel lblEarned   = makeLabel("", Font.PLAIN, 13);

    // Result tile row (5 coloured tiles showing score visually)
    private final JLabel[] scoreTiles = new JLabel[5];

    // Buttons
    private final JButton btnNewOrder = makeButton("New Order");
    private final JButton btnCook     = makeButton("Cook!");
    private final JButton btnSkip     = makeButton("Skip");
    private final JButton btnRecipes  = makeButton("Recipe Book");

    // Decoration buttons
    private final JToggleButton[] decoButtons = {
        makeToggle("Extra Spicy  +$2"),
        makeToggle("Premium  ×1.5"),
        makeToggle("Double  ×2"),
        makeToggle("Gluten-Free  +$3"),
    };

    // Message bar (bottom)
    private final JLabel lblMessage = makeLabel("Press 'New Order' to start!", Font.PLAIN, 13);

    // ------------------------------------------------------------------ //
    // Construction                                                        //
    // ------------------------------------------------------------------ //

    public SwingGame() {
        game.seedDefaultData();
        buildUi();
        updateStatusBar();
    }

    private void buildUi() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBackground(BG);
        frame.setLayout(new BorderLayout(0, 0));

        frame.add(buildStatusBar(), BorderLayout.NORTH);
        frame.add(buildCentrePanel(), BorderLayout.CENTER);
        frame.add(buildBottomBar(), BorderLayout.SOUTH);

        frame.pack();
        frame.setMinimumSize(new Dimension(520, 560));
        frame.setLocationRelativeTo(null);
    }

    // ---- Status bar -------------------------------------------------- //

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        bar.setBackground(ACCENT);
        JLabel title = makeLabel("Recipe Realm", Font.BOLD, 14);
        title.setForeground(Color.WHITE);
        bar.add(title);

        for (JLabel l : new JLabel[]{ lblSkill, lblEarnings, lblSat, lblXp }) {
            l.setForeground(new Color(220, 220, 220));
            bar.add(makeSeparator());
            bar.add(l);
        }
        return bar;
    }

    private JLabel makeSeparator() {
        JLabel sep = new JLabel("|");
        sep.setForeground(new Color(120, 120, 120));
        return sep;
    }

    // ---- Centre panel ------------------------------------------------ //

    private JPanel buildCentrePanel() {
        JPanel centre = new JPanel();
        centre.setBackground(BG);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));

        // Order card
        JPanel orderCard = buildCard();
        orderCard.setLayout(new BoxLayout(orderCard, BoxLayout.Y_AXIS));
        orderCard.add(centred(lblOrderTitle));
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(centred(lblRecipeName));
        orderCard.add(centred(lblDescription));
        orderCard.add(Box.createVerticalStrut(4));

        JPanel details = new JPanel(new GridLayout(2, 2, 8, 2));
        details.setOpaque(false);
        details.add(lblMethod);
        details.add(lblSkillReq);
        details.add(lblPrice);
        details.add(new JLabel());
        orderCard.add(details);

        // Decoration row
        orderCard.add(Box.createVerticalStrut(10));
        JPanel decoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        decoRow.setOpaque(false);
        for (JToggleButton b : decoButtons) decoRow.add(b);
        orderCard.add(decoRow);

        // Action buttons
        orderCard.add(Box.createVerticalStrut(10));
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionRow.setOpaque(false);
        actionRow.add(btnNewOrder);
        actionRow.add(btnCook);
        actionRow.add(btnSkip);
        actionRow.add(btnRecipes);
        orderCard.add(actionRow);

        centre.add(orderCard);
        centre.add(Box.createVerticalStrut(12));

        // Result card
        JPanel resultCard = buildCard();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));

        // Score tiles
        JPanel tileRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        tileRow.setOpaque(false);
        for (int i = 0; i < scoreTiles.length; i++) {
            JLabel tile = new JLabel("", SwingConstants.CENTER);
            tile.setOpaque(true);
            tile.setPreferredSize(new Dimension(48, 48));
            tile.setBorder(new LineBorder(BORDER_LIGHT, 2));
            tile.setBackground(BG);
            tile.setFont(new Font("SansSerif", Font.BOLD, 18));
            scoreTiles[i] = tile;
            tileRow.add(tile);
        }
        resultCard.add(tileRow);
        resultCard.add(centred(lblScore));
        resultCard.add(centred(lblStars));
        resultCard.add(centred(lblFeedback));
        resultCard.add(centred(lblEarned));

        centre.add(resultCard);
        return centre;
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    // ---- Bottom bar -------------------------------------------------- //

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        bar.setBackground(BG);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));
        bar.add(lblMessage);
        return bar;
    }

    // ------------------------------------------------------------------ //
    // Button wiring                                                       //
    // ------------------------------------------------------------------ //

    private void wireButtons() {
        btnNewOrder.addActionListener(e -> handleNewOrder());
        btnCook.addActionListener(e -> handleCook());
        btnSkip.addActionListener(e -> handleSkip());
        btnRecipes.addActionListener(e -> showRecipeBook());

        setCookingButtonsEnabled(false);
    }

    // ------------------------------------------------------------------ //
    // Handlers                                                            //
    // ------------------------------------------------------------------ //

    private void handleNewOrder() {
        List<Recipe> available = game.getRecipeService().getAvailableRecipes(game.getPlayerSkillLevel());
        if (available.isEmpty()) {
            setMessage("No recipes available at your skill level yet.");
            return;
        }
        pendingOrder = CustomerOrderFactory.createRandom(available);
        Recipe r = pendingOrder.getRequestedRecipe();

        lblOrderTitle.setText("Customer order incoming!");
        lblRecipeName.setText(r.getName() + "  [" + r.getCategory() + "]");
        lblDescription.setText(r.getDescription());
        lblMethod.setText("Method: " + r.getCookingStrategy().getMethodName());
        lblSkillReq.setText("Skill req: " + r.getRequiredSkillLevel() + "  /  Complexity: " + r.getComplexity());
        lblPrice.setText("Base price: $" + String.format("%.2f", r.getBasePrice()));

        clearResult();
        for (JToggleButton b : decoButtons) b.setSelected(false);
        setCookingButtonsEnabled(true);
        setMessage("Pick a variation (optional) then hit Cook!");
    }

    private void handleCook() {
        if (pendingOrder == null) return;

        Recipe base = pendingOrder.getRequestedRecipe();
        Recipe finalRecipe = applyDecoration(base);

        // Rebuild order with possibly-decorated recipe
        CustomerOrder order = recipeRealm.factory.CustomerOrderFactory.createForRecipe(finalRecipe);
        game.getOrderService().enqueueOrder(order);

        CookingResult result = game.processNextOrder();
        if (result == null) {
            setMessage("The order expired before cooking!");
            setCookingButtonsEnabled(false);
            pendingOrder = null;
            return;
        }

        ordersCompleted++;
        showResult(result, order);
        checkLevelUp();
        updateStatusBar();
        setCookingButtonsEnabled(false);
        pendingOrder = null;
    }

    private void handleSkip() {
        pendingOrder = null;
        lblOrderTitle.setText("Order skipped.");
        lblRecipeName.setText("");
        lblDescription.setText("");
        lblMethod.setText("");
        lblSkillReq.setText("");
        lblPrice.setText("");
        clearResult();
        for (JToggleButton b : decoButtons) b.setSelected(false);
        setCookingButtonsEnabled(false);
        setMessage("Skipped. Press 'New Order' for another customer.");
    }

    // ------------------------------------------------------------------ //
    // Result display                                                      //
    // ------------------------------------------------------------------ //

    private void showResult(CookingResult result, CustomerOrder order) {
        int score = result.getScore();
        int stars = result.getStarRating();

        // Score number
        lblScore.setText(score + " / 100");
        lblScore.setForeground(scoreColor(score));

        // Stars
        lblStars.setText("★".repeat(stars) + "☆".repeat(5 - stars));
        lblStars.setForeground(AMBER);

        // Feedback
        lblFeedback.setText(result.getFeedbackMessage());
        lblFeedback.setForeground(GRAY_DARK);

        // Earnings
        double earned = order.getRequestedRecipe().getBasePrice() * Math.max(0.2, score / 100.0);
        lblEarned.setText(result.isSuccess()
            ? String.format("Earned  $%.2f", earned)
            : "No earnings (failed)");
        lblEarned.setForeground(result.isSuccess() ? GREEN : GRAY_DARK);

        // Colour tiles (each tile = 20 points)
        for (int i = 0; i < scoreTiles.length; i++) {
            int threshold = (i + 1) * 20;
            if (score >= threshold) {
                scoreTiles[i].setBackground(tileColor(i, score));
                scoreTiles[i].setForeground(TEXT_ON_COLOR);
                scoreTiles[i].setText(String.valueOf((i + 1) * 20));
                scoreTiles[i].setBorder(new LineBorder(BORDER_MED, 2));
            } else {
                scoreTiles[i].setBackground(BG);
                scoreTiles[i].setForeground(GRAY_DARK);
                scoreTiles[i].setText("");
                scoreTiles[i].setBorder(new LineBorder(BORDER_LIGHT, 2));
            }
        }

        setMessage(result.isSuccess() ? "Great cook! Press 'New Order' to continue." : "Dish failed. Try again!");
    }

    private void clearResult() {
        lblScore.setText("");
        lblStars.setText("");
        lblFeedback.setText("");
        lblEarned.setText("");
        for (JLabel t : scoreTiles) {
            t.setText("");
            t.setBackground(BG);
            t.setBorder(new LineBorder(BORDER_LIGHT, 2));
        }
    }

    private Color scoreColor(int score) {
        if (score >= 90) return GREEN;
        if (score >= 60) return AMBER;
        return new Color(180, 80, 80);
    }

    private Color tileColor(int index, int score) {
        if (score >= 90) return GREEN;
        if (score >= 60) return AMBER;
        if (score >= 40) return BLUE;
        return GRAY_DARK;
    }

    // ------------------------------------------------------------------ //
    // Recipe book dialog                                                  //
    // ------------------------------------------------------------------ //

    private void showRecipeBook() {
        List<Recipe> all = game.getAllRecipes();
        int skill = game.getPlayerSkillLevel();

        String[] cols = { "Recipe", "Category", "Method", "Skill", "Price" };
        Object[][] data = all.stream().map(r -> new Object[]{
            (r.getRequiredSkillLevel() > skill ? "[locked] " : "") + r.getName(),
            r.getCategory(),
            r.getCookingStrategy().getMethodName(),
            r.getRequiredSkillLevel(),
            String.format("$%.2f", r.getBasePrice())
        }).toArray(Object[][]::new);

        JTable table = new JTable(data, cols);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.setEnabled(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(500, 220));

        JOptionPane.showMessageDialog(frame, scroll, "Recipe Book", JOptionPane.PLAIN_MESSAGE);
    }

    // ------------------------------------------------------------------ //
    // Decoration                                                          //
    // ------------------------------------------------------------------ //

    private Recipe applyDecoration(Recipe base) {
        Recipe r = base;
        if (decoButtons[0].isSelected()) r = new ExtraSpicyDecorator(r);
        if (decoButtons[1].isSelected()) r = new PremiumDecorator(r);
        if (decoButtons[2].isSelected()) r = new DoubleServingDecorator(r);
        if (decoButtons[3].isSelected()) r = new GlutenFreeDecorator(r);
        return r;
    }

    // ------------------------------------------------------------------ //
    // Progression                                                         //
    // ------------------------------------------------------------------ //

    private void checkLevelUp() {
        int current = game.getPlayerSkillLevel();
        if (current >= 10) return;
        if (ordersCompleted >= XP_THRESHOLDS[current]) {
            game.levelUp();
            setMessage("★ LEVEL UP! You are now a skill-" + game.getPlayerSkillLevel() + " chef!");
        }
    }

    private void updateStatusBar() {
        int skill = game.getPlayerSkillLevel();
        lblSkill.setText("Skill: Lv." + skill);
        lblEarnings.setText("Earned: $" + String.format("%.2f", game.getTotalEarnings()));
        lblSat.setText("Satisfaction: " + game.getAverageSatisfaction() + "%");
        int next = skill < 10 ? XP_THRESHOLDS[skill] : -1;
        lblXp.setText(next > 0 ? "XP: " + ordersCompleted + "/" + next : "MAX LEVEL");
    }

    // ------------------------------------------------------------------ //
    // Helpers                                                             //
    // ------------------------------------------------------------------ //

    private void setCookingButtonsEnabled(boolean enabled) {
        btnCook.setEnabled(enabled);
        btnSkip.setEnabled(enabled);
        for (JToggleButton b : decoButtons) b.setEnabled(enabled);
    }

    private void setMessage(String text) {
        lblMessage.setText(text);
    }

    private static JLabel makeLabel(String text, int style, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        return l;
    }

    private static JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        return b;
    }

    private static JToggleButton makeToggle(String text) {
        JToggleButton b = new JToggleButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setFocusPainted(false);
        return b;
    }

    private static JPanel centred(JLabel label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        p.setOpaque(false);
        p.add(label);
        return p;
    }

    // ------------------------------------------------------------------ //
    // Entry point                                                         //
    // ------------------------------------------------------------------ //

    public void show() {
        wireButtons();
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public static void main(String[] args) {
        new SwingGame().show();
    }
}