package net.flinkgutt.samples.nodes.view.product;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventJXTableModel;
import java.util.Collection;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.flinkgutt.samples.nodes.api.ICategory;
import net.flinkgutt.samples.nodes.api.IProduct;
import net.flinkgutt.samples.nodes.api.IProductDAO;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//net.flinkgutt.samples.nodes.view.product//ProductEditor//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ProductEditorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "net.flinkgutt.samples.nodes.view.product.ProductEditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ProductEditorAction",
        preferredID = "ProductEditorTopComponent")
@Messages({
    "CTL_ProductEditorAction=ProductEditor",
    "CTL_ProductEditorTopComponent=ProductEditor Window",
    "HINT_ProductEditorTopComponent=This is a ProductEditor window"
})
public final class ProductEditorTopComponent extends TopComponent implements LookupListener {
    private IProductDAO productDAO = Lookup.getDefault().lookup(IProductDAO.class);
    private EventList<IProduct> productEventList = new BasicEventList<IProduct>();
    private EventJXTableModel<IProduct> productsTableModel = new EventJXTableModel<IProduct>(productEventList,new AdvancedProductsTableFormat());
    private Lookup.Result<ICategory> categoryResult = null;
    
    public ProductEditorTopComponent() {
        initComponents();
        setName(Bundle.CTL_ProductEditorTopComponent());
        setToolTipText(Bundle.HINT_ProductEditorTopComponent());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPaneProductTable = new javax.swing.JScrollPane();
        productTable = new org.jdesktop.swingx.JXTable();
        productDescriptionLabel = new javax.swing.JLabel();
        scrollPaneProductDescription = new javax.swing.JScrollPane();
        productDescriptionArea = new javax.swing.JTextArea();

        productTable.setModel(productsTableModel);
        productTable.setHighlighters(HighlighterFactory.createSimpleStriping());
        scrollPaneProductTable.setViewportView(productTable);
        productTable.getSelectionModel().addListSelectionListener(new ProductRowSelectionListener());

        org.openide.awt.Mnemonics.setLocalizedText(productDescriptionLabel, org.openide.util.NbBundle.getMessage(ProductEditorTopComponent.class, "ProductEditorTopComponent.productDescriptionLabel.text")); // NOI18N

        productDescriptionArea.setColumns(20);
        productDescriptionArea.setRows(5);
        scrollPaneProductDescription.setViewportView(productDescriptionArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneProductTable, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(productDescriptionLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(scrollPaneProductDescription, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollPaneProductTable, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(productDescriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneProductDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea productDescriptionArea;
    private javax.swing.JLabel productDescriptionLabel;
    private org.jdesktop.swingx.JXTable productTable;
    private javax.swing.JScrollPane scrollPaneProductDescription;
    private javax.swing.JScrollPane scrollPaneProductTable;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // We add this TopComponent as a LookupListener every time this TC is opened. See componentClosed().
        categoryResult = Utilities.actionsGlobalContext().lookupResult(ICategory.class);
        categoryResult.addLookupListener (this);
    }

    @Override
    public void componentClosed() {
        // We remove this TopComponent as a LookupListener every time this TC is closed.
        categoryResult.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends ICategory> catRes =  categoryResult.allInstances();
        if( catRes.size() == 1)  {
            ICategory category = catRes.iterator().next();
            if (category == null) {
                return;
            }
            productDescriptionArea.setText(""); // Clear the text area when we select something else. it just looks wierd otherwise.
            // TODO if the state of the description is dirty we should ask what to do before we do anything.
            try {
            productEventList.getReadWriteLock().writeLock().lock();
            productEventList.clear();
            productEventList.addAll( productDAO.getProducts(category) );
            } finally {
                productEventList.getReadWriteLock().writeLock().unlock();
            }
        }
        
    }    
    
    private class ProductRowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            if (productTable.getSelectedRowCount() > 0) {
                final int modelRowNum = productTable.convertRowIndexToModel(productTable.getSelectedRow());
                final IProduct p = productsTableModel.getElementAt(modelRowNum);
                productDescriptionArea.setText(p.getDescription());
            }
        }
    }
}