package ro.ase.proiect.ui.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.JFrame;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Fereastră GUI (JFrame) care afișează un grafic (Bar Chart)
 * pentru intrări și ieșiri zilnice, folosind biblioteca JFreeChart.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 4.11.2025
 */
public class FereastraGrafic extends JFrame {

    /**
     * Constructorul ferestrei de grafic.
     * Inițializează, configurează și afișează fereastra cu graficul de bare.
     *
     * @param statisticiZilnice Map&lt;String, double[]&gt; cu "intrari" si "iesiri"
     * @param numeClient        Numele clientului pentru titlu.
     * @param zileInUrma        Numărul de zile reprezentate în grafic.
     */
    public FereastraGrafic(Map<String, double[]> statisticiZilnice, String numeClient, int zileInUrma) {
        setTitle("Grafic Flux Financiar (Ultimele " + zileInUrma + " zile) - " + numeClient);
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultCategoryDataset dataset = createDataset(statisticiZilnice, zileInUrma);

        JFreeChart barChart = ChartFactory.createBarChart(
                "Evoluția Fluxului Financiar Zilnic ",
                "Ziua",
                "Suma ",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();


        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        domainAxis.setTickLabelFont(labelFont);

        ChartPanel chartPanel = new ChartPanel(barChart);
        add(chartPanel);
        setVisible(true);
        setState(Frame.NORMAL);
        toFront();
        requestFocus();
    }

    /**
     * Metodă ajutătoare care convertește Map-ul de double[] într-un set de date pe care JFreeChart îl înțelege.
     * Afișează zilele de la cea mai veche la cea mai nouă pe axa X (cronologic).
     *
     * @param statistici Map-ul (cheile "intrari", "iesiri") cu vectorii de sume zilnice.
     * @param zileInUrma Numărul de zile de procesat.
     * @return Un {@link DefaultCategoryDataset} populat pentru grafic.
     */
    private DefaultCategoryDataset createDataset(Map<String, double[]> statistici, int zileInUrma) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate azi = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM");
        double[] intrari = statistici.get("intrari");
        double[] iesiri = statistici.get("iesiri");

        for (int i = zileInUrma - 1; i >= 0; i--) {
            String ziua = azi.minusDays(i).format(formatter);
            dataset.addValue(intrari[i], "Intrări", ziua);
            dataset.addValue(iesiri[i], "Ieșiri", ziua);
        }

        return dataset;
    }
}
