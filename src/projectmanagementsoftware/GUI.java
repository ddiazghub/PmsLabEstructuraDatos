/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package projectmanagementsoftware;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import projectmanagementsoftware.linkedlist.LinkedList;
import projectmanagementsoftware.Project;
import projectmanagementsoftware.gui.ProjectFileSystemTree;
import projectmanagementsoftware.gui.ProjectFileSystemTreeNode;
import projectmanagementsoftware.gui.SchedulePanel;
import projectmanagementsoftware.gui.WBSAnimationPanel;
import projectmanagementsoftware.linkedlist.LinkedListNode;
import projectmanagementsoftware.utils.Validators;
import projectmanagementsoftware.wbs.Deliverable;
import projectmanagementsoftware.wbs.WBSNode;
import projectmanagementsoftware.wbs.WorkPackage;

/**
 *
 * @author david
 */
public class GUI extends javax.swing.JFrame {
    private LinkedList<Project> projects;
    private DefaultListModel<String> memberListModel;
    private ProjectFileSystemTree tree;
    private CardLayout cards;
    private LinkedList<String> tabs;
    
    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        
        this.tabs = new LinkedList<>();
        this.projects = Project.load();
        this.memberListModel = new DefaultListModel<>();
        this.projectMembersList.setModel(this.memberListModel);
        this.mainContentTabPane.removeAll();
        this.tree = new ProjectFileSystemTree();
        this.fileExplorerPanel.add(this.tree);
        this.cards = (CardLayout) this.nodeDataPanel.getLayout();
        this.cards.show(this.nodeDataPanel, "none");
        
        if (this.projects.length() > 0)
            this.setProjectData(this.projects.get(0).getName());
        
        final GUI gui = this;
        
        this.tree.setSecondaryClickListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                WBSNode node = (WBSNode) e.getSource();
                
                if (node instanceof WorkPackage) {
                    gui.setWorkPackageData(node.getName(), node.getPath());
                } else if (node instanceof Deliverable) {
                    gui.setDeliverableData(node.getName(), ((Deliverable) node).getDescription(), node.getPath());
                } else {
                    gui.setProjectData(node.getName());
                }
            }
        });
        
        this.updateUI();
    }

    private void updateUI() {
        this.tree.setProjects(this.projects);
    }
    
    public void showCreateProjectDialog() {
        this.projectNameField.setText("");
        this.addMemberField.setText("");
        this.memberListModel.clear();
        this.newProjectDialog.setLocationRelativeTo(this);
        this.newProjectDialog.setVisible(true);
    }
    
    public void showAddWorkPackageDialog() {
        WorkPackage parent = (WorkPackage) this.getSelectedWorkPackage();
        
        if (parent == null)
            return;
        
        this.workPackageParentField.setText(parent.getPath());
        this.workPackageNameField.setText("");
        this.newWorkPackageDialog.setLocationRelativeTo(this);
        this.newWorkPackageDialog.setVisible(true);
    }
    
    public void showAddDeliverableDialog() {
        WorkPackage parent = (WorkPackage) this.getSelectedWorkPackage();
        
        if (parent == null)
            return;
        
        this.deliverableParentField.setText(parent.getPath());
        this.deliverableNameField.setText("");
        this.deliverableDescriptionArea.setText("");
        this.newDeliverableDialog.setLocationRelativeTo(this);
        this.newDeliverableDialog.setVisible(true);
    }
    
    public boolean validateName(String name) {
        if (Validators.isValidFileName(name))
            return true;
        
        showError("El nombre contiene caracteres no permitidos");
        
        return false;
    }
    
    public boolean validateUnique(String name, String path) {
        if (Validators.isUnique(name, path))
            return true;
        
        showError("Ya existe un elemento con el mismo nombre");
        
        return false;
    }
    
    public WorkPackage getSelectedWorkPackage() {
        WBSNode node = this.getSelectedNode("Selecciona un paquete de trabajo en el árbol de la izquierda para agregar un nodo");
        
        if (node == null)
            return null;
        
        if (node instanceof Deliverable) {
            showError("El nodo padre debe ser un paquete de trabajo");
            return null;
        }
        
        return (WorkPackage) node;
    }
    
    public WBSNode getSelectedNode(String errorMsg) {
        ProjectFileSystemTreeNode node = this.tree.getSelected();
        
        if (node == null) {
            showError(errorMsg);
            return null;
        }
            
        return node.get();
    }
    
    public void setDeliverableData(String name, String description, String path) {
        this.cards.show(this.nodeDataPanel, "deliverable");
        this.deliverableNameLabel.setText(name);
        this.deliverableDescrArea.setText(description);
        this.deliverablePathArea.setText(path);
        String projectName = LinkedList.split(path, "/").get(0);
        this.deliverableProjectLabel.setText(projectName);
    }
    
    public void setWorkPackageData(String name, String path) {
        this.cards.show(this.nodeDataPanel, "workPackage");
        this.workPkgNameLabel.setText(name);
        this.workPkgPathLabel.setText(path);
        String projectName = LinkedList.split(path, "/").get(0);
        this.workPkgProjectLabel.setText(projectName);
    }
    
    public void setProjectData(String name) {
        Project project = this.getProject(name);
        
        if (project == null)
            return;
        
        this.cards.show(this.nodeDataPanel, "project");
        this.projectNameLabel.setText(name);
        this.projectMembersArea.setText("");
        
        project.getTeam().forEach(member -> {
            this.projectMembersArea.setText(this.projectMembersArea.getText() + member + "\n");
        });
    }
    
    public void showError(String message) {
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }
    
    public Project getProject(String projectName) {
        LinkedListNode<Project> container = new LinkedListNode<>(null);
        
        projects.forEach(project -> {
            if (project.getName().equals(projectName)) {
                container.set(project);
            }
        });
        
        return container.get();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        newProjectDialog = new javax.swing.JDialog();
        addMemberField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        projectMembersList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        projectNameField = new javax.swing.JTextField();
        addMemberButton = new javax.swing.JButton();
        cancelNewProjectButton = new javax.swing.JButton();
        confirmNewProjectButton = new javax.swing.JButton();
        removeMemberButton = new javax.swing.JButton();
        newWorkPackageDialog = new javax.swing.JDialog();
        workPackageParentField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        workPackageNameField = new javax.swing.JTextField();
        cancelNewWorkPackageButton = new javax.swing.JButton();
        confirmNewWorkPackageButton = new javax.swing.JButton();
        newDeliverableDialog = new javax.swing.JDialog();
        deliverableParentField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        deliverableNameField = new javax.swing.JTextField();
        cancelNewDeliverableButton = new javax.swing.JButton();
        confirmNewDeliverableButton = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        deliverableDescriptionArea = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        header = new javax.swing.JPanel();
        newProjectButton = new javax.swing.JButton();
        wbsButton = new javax.swing.JButton();
        scheduleButton = new javax.swing.JButton();
        addWorkPackage = new javax.swing.JButton();
        addDeliverable = new javax.swing.JButton();
        panel1 = new javax.swing.JPanel();
        sidebar = new javax.swing.JPanel();
        fileExplorerPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        nodeDataPanel = new javax.swing.JPanel();
        noProjectPanel = new javax.swing.JPanel();
        deliverableCard = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        deliverableNameLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        deliverableDescrArea = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        deliverableProjectLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        deliverablePathArea = new javax.swing.JTextArea();
        workPackageCard = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        workPkgNameLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        workPkgProjectLabel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        workPkgPathLabel = new javax.swing.JTextArea();
        projectCard = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        projectNameLabel = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        projectMembersArea = new javax.swing.JTextArea();
        jLabel17 = new javax.swing.JLabel();
        panel2 = new javax.swing.JPanel();
        mainContentTabPane = new javax.swing.JTabbedPane();

        newProjectDialog.setTitle("Nuevo Proyecto");
        newProjectDialog.setAlwaysOnTop(true);
        newProjectDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        newProjectDialog.setMinimumSize(new java.awt.Dimension(365, 364));
        newProjectDialog.setModal(true);
        newProjectDialog.setResizable(false);
        newProjectDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addMemberField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMemberFieldActionPerformed(evt);
            }
        });
        newProjectDialog.getContentPane().add(addMemberField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 180, 30));

        jLabel1.setText("Integrantes");
        newProjectDialog.getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        jScrollPane3.setViewportView(projectMembersList);

        newProjectDialog.getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 260, 120));

        jLabel2.setText("Nombre del proyecto");
        newProjectDialog.getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));
        newProjectDialog.getContentPane().add(projectNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 260, 30));

        addMemberButton.setText("+");
        addMemberButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMemberButtonActionPerformed(evt);
            }
        });
        newProjectDialog.getContentPane().add(addMemberButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 110, 40, 30));

        cancelNewProjectButton.setText("Cancelar");
        cancelNewProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewProjectButtonActionPerformed(evt);
            }
        });
        newProjectDialog.getContentPane().add(cancelNewProjectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 280, 120, 40));

        confirmNewProjectButton.setText("Confirmar");
        confirmNewProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmNewProjectButtonActionPerformed(evt);
            }
        });
        newProjectDialog.getContentPane().add(confirmNewProjectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, 120, 40));

        removeMemberButton.setText("-");
        removeMemberButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMemberButtonActionPerformed(evt);
            }
        });
        newProjectDialog.getContentPane().add(removeMemberButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, 40, 30));

        newWorkPackageDialog.setTitle("Nuevo Paquete de Trabajo");
        newWorkPackageDialog.setAlwaysOnTop(true);
        newWorkPackageDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        newWorkPackageDialog.setMinimumSize(new java.awt.Dimension(365, 236));
        newWorkPackageDialog.setModal(true);
        newWorkPackageDialog.setResizable(false);
        newWorkPackageDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        workPackageParentField.setEditable(false);
        workPackageParentField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workPackageParentFieldActionPerformed(evt);
            }
        });
        newWorkPackageDialog.getContentPane().add(workPackageParentField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 260, 30));

        jLabel3.setText("Padre");
        newWorkPackageDialog.getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        jLabel4.setText("Nombre de paquete de trabajo");
        newWorkPackageDialog.getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));
        newWorkPackageDialog.getContentPane().add(workPackageNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 260, 30));

        cancelNewWorkPackageButton.setText("Cancelar");
        cancelNewWorkPackageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewWorkPackageButtonActionPerformed(evt);
            }
        });
        newWorkPackageDialog.getContentPane().add(cancelNewWorkPackageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, 120, 40));

        confirmNewWorkPackageButton.setText("Confirmar");
        confirmNewWorkPackageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmNewWorkPackageButtonActionPerformed(evt);
            }
        });
        newWorkPackageDialog.getContentPane().add(confirmNewWorkPackageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 120, 40));

        newDeliverableDialog.setTitle("Nuevo Entregable");
        newDeliverableDialog.setAlwaysOnTop(true);
        newDeliverableDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        newDeliverableDialog.setMinimumSize(new java.awt.Dimension(365, 390));
        newDeliverableDialog.setModal(true);
        newDeliverableDialog.setResizable(false);
        newDeliverableDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        deliverableParentField.setEditable(false);
        deliverableParentField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deliverableParentFieldActionPerformed(evt);
            }
        });
        newDeliverableDialog.getContentPane().add(deliverableParentField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 260, 30));

        jLabel5.setText("Descripción");
        newDeliverableDialog.getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, -1, -1));

        jLabel6.setText("Nombre del Entregable");
        newDeliverableDialog.getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));
        newDeliverableDialog.getContentPane().add(deliverableNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 260, 30));

        cancelNewDeliverableButton.setText("Cancelar");
        cancelNewDeliverableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewDeliverableButtonActionPerformed(evt);
            }
        });
        newDeliverableDialog.getContentPane().add(cancelNewDeliverableButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 320, 120, 40));

        confirmNewDeliverableButton.setText("Confirmar");
        confirmNewDeliverableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmNewDeliverableButtonActionPerformed(evt);
            }
        });
        newDeliverableDialog.getContentPane().add(confirmNewDeliverableButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 120, 40));

        deliverableDescriptionArea.setColumns(20);
        deliverableDescriptionArea.setRows(5);
        jScrollPane5.setViewportView(deliverableDescriptionArea);

        newDeliverableDialog.getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 260, 130));

        jLabel8.setText("Padre");
        newDeliverableDialog.getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        header.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        header.setPreferredSize(new java.awt.Dimension(1280, 80));
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.LINE_AXIS));

        newProjectButton.setText("Nuevo Proyecto");
        newProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectButtonActionPerformed(evt);
            }
        });
        header.add(newProjectButton);

        wbsButton.setText("EDT");
        wbsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wbsButtonActionPerformed(evt);
            }
        });
        header.add(wbsButton);

        scheduleButton.setText("Cronograma");
        scheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleButtonActionPerformed(evt);
            }
        });
        header.add(scheduleButton);

        addWorkPackage.setText("Nuevo Paquete de trabajo");
        addWorkPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWorkPackageActionPerformed(evt);
            }
        });
        header.add(addWorkPackage);

        addDeliverable.setText("Nuevo Entregable");
        addDeliverable.setToolTipText("");
        addDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDeliverableActionPerformed(evt);
            }
        });
        header.add(addDeliverable);

        getContentPane().add(header, java.awt.BorderLayout.NORTH);

        panel1.setLayout(new javax.swing.BoxLayout(panel1, javax.swing.BoxLayout.LINE_AXIS));

        sidebar.setMaximumSize(new java.awt.Dimension(250, 640));
        sidebar.setMinimumSize(new java.awt.Dimension(200, 0));
        sidebar.setPreferredSize(new java.awt.Dimension(250, 640));
        sidebar.setLayout(new javax.swing.BoxLayout(sidebar, javax.swing.BoxLayout.Y_AXIS));

        fileExplorerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fileExplorerPanel.setMaximumSize(new java.awt.Dimension(4356345, 45634564));
        fileExplorerPanel.setMinimumSize(new java.awt.Dimension(250, 0));
        fileExplorerPanel.setPreferredSize(new java.awt.Dimension(250, 336));
        fileExplorerPanel.setLayout(new javax.swing.BoxLayout(fileExplorerPanel, javax.swing.BoxLayout.PAGE_AXIS));
        sidebar.add(fileExplorerPanel);

        nodeDataPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        nodeDataPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout noProjectPanelLayout = new javax.swing.GroupLayout(noProjectPanel);
        noProjectPanel.setLayout(noProjectPanelLayout);
        noProjectPanelLayout.setHorizontalGroup(
            noProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        noProjectPanelLayout.setVerticalGroup(
            noProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        nodeDataPanel.add(noProjectPanel, "none");

        deliverableCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Nombre:");
        deliverableCard.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        deliverableNameLabel.setText("j");
        deliverableCard.add(deliverableNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 210, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Descripción:");
        deliverableCard.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        deliverableDescrArea.setColumns(20);
        deliverableDescrArea.setRows(5);
        jScrollPane2.setViewportView(deliverableDescrArea);

        deliverableCard.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 210, 60));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Proyecto:");
        deliverableCard.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        deliverableProjectLabel.setText("j");
        deliverableCard.add(deliverableProjectLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 210, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Ruta:");
        deliverableCard.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        deliverablePathArea.setColumns(20);
        deliverablePathArea.setRows(5);
        jScrollPane8.setViewportView(deliverablePathArea);

        deliverableCard.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 210, 40));

        nodeDataPanel.add(deliverableCard, "deliverable");

        workPackageCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Nombre:");
        workPackageCard.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        workPkgNameLabel.setText("j");
        workPackageCard.add(workPkgNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 210, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Proyecto:");
        workPackageCard.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        workPkgProjectLabel.setText("j");
        workPackageCard.add(workPkgProjectLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 210, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Ruta:");
        workPackageCard.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        workPkgPathLabel.setColumns(20);
        workPkgPathLabel.setRows(5);
        jScrollPane7.setViewportView(workPkgPathLabel);

        workPackageCard.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 200, 40));

        nodeDataPanel.add(workPackageCard, "workPackage");

        projectCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Nombre:");
        projectCard.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        projectNameLabel.setText("j");
        projectCard.add(projectNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 220, -1));

        projectMembersArea.setColumns(20);
        projectMembersArea.setRows(5);
        jScrollPane6.setViewportView(projectMembersArea);

        projectCard.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 210, 90));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("Integrantes:");
        projectCard.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        nodeDataPanel.add(projectCard, "project");

        jScrollPane1.setViewportView(nodeDataPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        sidebar.add(jPanel1);

        panel1.add(sidebar);

        panel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panel2.setLayout(new java.awt.BorderLayout());
        panel2.add(mainContentTabPane, java.awt.BorderLayout.CENTER);

        panel1.add(panel2);

        getContentPane().add(panel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectButtonActionPerformed
        //String projectName = JOptionPane.showInputDialog(this, "Nombre del proyecto", "Nuevo proyecto", JOptionPane.QUESTION_MESSAGE);
        //projects.add(new Project(projectName));
        this.showCreateProjectDialog();
    }//GEN-LAST:event_newProjectButtonActionPerformed

    private void addMemberFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMemberFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addMemberFieldActionPerformed

    private void confirmNewProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmNewProjectButtonActionPerformed
        String projectName = this.projectNameField.getText();
        
        if (!validateName(projectName) || !validateUnique(projectName, "/"))
            return;
        
        LinkedList<String> team = new LinkedList<>();
        
        for (Object member : this.memberListModel.toArray()) {
            team.add((String) member);
        }
        
        this.projects.add(Project.create(projectName, team));
        this.newProjectDialog.setVisible(false);
        this.updateUI();
    }//GEN-LAST:event_confirmNewProjectButtonActionPerformed

    private void cancelNewProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewProjectButtonActionPerformed
        this.newProjectDialog.setVisible(false);
    }//GEN-LAST:event_cancelNewProjectButtonActionPerformed

    private void addMemberButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMemberButtonActionPerformed
        String newMember = this.addMemberField.getText();
        
        if (newMember.equals("")) {
            showError("No se ha ingresado un nombre");
            return;
        }
        
        this.memberListModel.addElement(newMember);
        this.addMemberField.setText("");
    }//GEN-LAST:event_addMemberButtonActionPerformed

    private void removeMemberButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMemberButtonActionPerformed
        for (String member : this.projectMembersList.getSelectedValuesList()) {
            this.memberListModel.removeElement(member);
        }
    }//GEN-LAST:event_removeMemberButtonActionPerformed

    private void addWorkPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWorkPackageActionPerformed
        this.showAddWorkPackageDialog();
    }//GEN-LAST:event_addWorkPackageActionPerformed

    private void workPackageParentFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workPackageParentFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workPackageParentFieldActionPerformed

    private void cancelNewWorkPackageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewWorkPackageButtonActionPerformed
        this.newWorkPackageDialog.setVisible(false);
    }//GEN-LAST:event_cancelNewWorkPackageButtonActionPerformed

    private void confirmNewWorkPackageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmNewWorkPackageButtonActionPerformed
        String name = this.workPackageNameField.getText();
        String parentPath = this.workPackageParentField.getText();
        
        if (!validateName(name) || !validateUnique(name, parentPath))
            return;
        
        String projectName = parentPath.split("/")[0];

        projects.forEach(project -> {
            if (project.getName().equals(projectName)) {
                project.getWbs().add(WorkPackage.create(name, parentPath + "/" + name));
            }
        });

        this.newWorkPackageDialog.setVisible(false);
        this.updateUI();
    }//GEN-LAST:event_confirmNewWorkPackageButtonActionPerformed

    private void deliverableParentFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deliverableParentFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deliverableParentFieldActionPerformed

    private void cancelNewDeliverableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewDeliverableButtonActionPerformed
        this.newDeliverableDialog.setVisible(false);
    }//GEN-LAST:event_cancelNewDeliverableButtonActionPerformed

    private void confirmNewDeliverableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmNewDeliverableButtonActionPerformed
        String name = this.deliverableNameField.getText();
        String parentPath = this.deliverableParentField.getText();
        
        if (!validateName(name) || !validateUnique(name, parentPath))
            return;
        
        String description = this.deliverableDescriptionArea.getText();
        String projectName = parentPath.split("/")[0];

        projects.forEach(project -> {
            if (project.getName().equals(projectName)) {
                project.getWbs().add(Deliverable.create(name, parentPath + "/" + name + ".txt", description));
            }
        });

        this.newDeliverableDialog.setVisible(false);
        this.updateUI();
    }//GEN-LAST:event_confirmNewDeliverableButtonActionPerformed

    private void addDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDeliverableActionPerformed
        this.showAddDeliverableDialog();
    }//GEN-LAST:event_addDeliverableActionPerformed

    private void wbsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wbsButtonActionPerformed
        WBSNode selected = this.getSelectedNode("Selecciona un elemento que pertenezca a un proyecto en el árbol de la izquierda para ver su EDT");
        
        if (selected == null)
            return;
        
        String tabname = "EDT " + selected.getProjectName();
        
        if (this.tabs.contains(tabname))
            return;
        
        this.mainContentTabPane.addTab(tabname, new WBSAnimationPanel());
        this.tabs.add(tabname);
    }//GEN-LAST:event_wbsButtonActionPerformed

    private void scheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleButtonActionPerformed
        WBSNode selected = this.getSelectedNode("Selecciona un elemento que pertenezca a un proyecto en el árbol de la izquierda para ver su Cronograma");
        
        if (selected == null)
            return;
        String tabname = "Cronograma " + selected.getProjectName();
        
        if (this.tabs.contains(tabname))
            return;
        
        this.mainContentTabPane.addTab(tabname, new SchedulePanel());
        this.tabs.add(tabname);
    }//GEN-LAST:event_scheduleButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDeliverable;
    private javax.swing.JButton addMemberButton;
    private javax.swing.JTextField addMemberField;
    private javax.swing.JButton addWorkPackage;
    private javax.swing.JButton cancelNewDeliverableButton;
    private javax.swing.JButton cancelNewProjectButton;
    private javax.swing.JButton cancelNewWorkPackageButton;
    private javax.swing.JButton confirmNewDeliverableButton;
    private javax.swing.JButton confirmNewProjectButton;
    private javax.swing.JButton confirmNewWorkPackageButton;
    private javax.swing.JPanel deliverableCard;
    private javax.swing.JTextArea deliverableDescrArea;
    private javax.swing.JTextArea deliverableDescriptionArea;
    private javax.swing.JTextField deliverableNameField;
    private javax.swing.JLabel deliverableNameLabel;
    private javax.swing.JTextField deliverableParentField;
    private javax.swing.JTextArea deliverablePathArea;
    private javax.swing.JLabel deliverableProjectLabel;
    private javax.swing.JPanel fileExplorerPanel;
    private javax.swing.JPanel header;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane mainContentTabPane;
    private javax.swing.JDialog newDeliverableDialog;
    private javax.swing.JButton newProjectButton;
    private javax.swing.JDialog newProjectDialog;
    private javax.swing.JDialog newWorkPackageDialog;
    private javax.swing.JPanel noProjectPanel;
    private javax.swing.JPanel nodeDataPanel;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel projectCard;
    private javax.swing.JTextArea projectMembersArea;
    private javax.swing.JList<String> projectMembersList;
    private javax.swing.JTextField projectNameField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JButton removeMemberButton;
    private javax.swing.JButton scheduleButton;
    private javax.swing.JPanel sidebar;
    private javax.swing.JButton wbsButton;
    private javax.swing.JPanel workPackageCard;
    private javax.swing.JTextField workPackageNameField;
    private javax.swing.JTextField workPackageParentField;
    private javax.swing.JLabel workPkgNameLabel;
    private javax.swing.JTextArea workPkgPathLabel;
    private javax.swing.JLabel workPkgProjectLabel;
    // End of variables declaration//GEN-END:variables
}