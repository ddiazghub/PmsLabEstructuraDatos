package projectmanagementsoftware.wbs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.tree.DefaultMutableTreeNode;
import projectmanagementsoftware.Project;
import projectmanagementsoftware.linkedlist.LinkedList;
import projectmanagementsoftware.linkedlist.LinkedListNode;
import projectmanagementsoftware.tree.Tree;
import projectmanagementsoftware.tree.TreeNode;
import projectmanagementsoftware.utils.FileHelpers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * EDT de un proyecto. Representada mediante un árbol N-Ario.
 * @author david
 */
public class WorkBreakdownStructure extends Tree<WBSNode> {
    /**
     * El proyecto asociado a la EDT.
     */
    private Project project;
    
    /**
     * Crea una nueva EDT para el proyecto pasado como parámetro.
     * @param project Proyecto para el cual es la EDT.
     */
    private WorkBreakdownStructure(Project project) {
        super(new TreeNode<>(new WorkPackage(project.getName(), project.getName())));
        
        this.project = project;
    }

    /**
     * Obtiene el proyecto asociado a la EDT.
     * @return 
     */
    public Project getProject() {
        return project;
    }
    
    public void add(WBSNode node) {
        LinkedList<String> path = LinkedList.split(node.getPath(), "/");
        path.remove(0);
        add(this.getRoot(), node, path);
        this.writeToFile();
    }
    
    public static void add(TreeNode<WBSNode> current, WBSNode node, LinkedList<String> path) {
        if (path.length() == 1) {
            current.addChild(node);
            
            return;
        }
        
        current.getChildren().forEach(child -> {
            if (child.get().getName().equals(path.getHead().get())) {
                path.remove(0);
                add(child, node, path);
            }
        });
    }
    
    public WBSNode find(String path) {
        LinkedList<String> nodePath = LinkedList.split(path, "/");
        nodePath.remove(0);
        return find(this.getRoot(), nodePath);
    }
    
    public WBSNode find(TreeNode<WBSNode> current, LinkedList<String> path) {
        if (path.length() == 0) {
            
            return current.get();
        }
        
        current.getChildren().forEach(child -> {
            if (child.get().getName().equals(path.getHead().get())) {
                path.remove(0);
                find(child, path);
            }
        });
        
        return null;
    }
    
    
    public LinkedList<Deliverable> getDeliverables() {
        LinkedList<Deliverable> deliverables = new LinkedList<>();
        
        this.preorder(node -> {
            if (node instanceof Deliverable)
                deliverables.add((Deliverable) node);
        });
        
        return deliverables;
    }
    
    public void writeToFile() {
        File wbsTxt = FileHelpers.get(this.project.getName() + "/EDT.txt");
        
        try (FileWriter writer = new FileWriter(wbsTxt)) {
            writer.write(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void save() {
        File root = FileHelpers.get(this.project.getName() + "/wbs");
        FileHelpers.clearDirectory(root);
        
        this.getRoot().getChildren().forEach(
            child -> save(child, root.getPath() + "/" + child.get().getName())
        );
    }
    
    @Override
    public String toString() {
        final LinkedListNode<String> string = new LinkedListNode<>("");
        
        this.mapWithLevelCount((node, level) -> {
            String line = "";
            
            for (int i = 0; i < level; i++) {
                line += "\t";
            }
            
            line += node.getName() + "\n";
            string.set(string.get() + line);
            
            return null;
        });
                
        return string.get();
    }
    
    private static void save(TreeNode<WBSNode> node, String path) {
        File file = FileHelpers.get(path);
        
        if (node.get() instanceof WorkPackage) {
            file.mkdir();
            
            node.getChildren().forEach(child -> {
                String childPath = path + "/" + child.get().getName();
                
                if (child.get() instanceof Deliverable)
                    childPath += ".txt";
                
                save(child, childPath);
            });
        }
        
        if (node.get() instanceof Deliverable) {
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(((Deliverable) node.get()).getDescription());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static WorkBreakdownStructure load(Project project) {
        File root = FileHelpers.get(project.getName() + "/wbs");
        WorkBreakdownStructure wbs = new WorkBreakdownStructure(project);
        
        if (!root.exists()) {
            FileHelpers.clearDirectory(FileHelpers.get(project.getName()));
            return wbs;
        }
        
        for (File file : root.listFiles()) {
            wbs.getRoot().addChild(buildWBS(file, project.getName() + "/wbs/" + file.getName(), project));
        }
        
        return wbs;
    }
    
    private static TreeNode<WBSNode> buildWBS(File file, String path, Project project) {
        LinkedList<String> nodePath = LinkedList.split(path, "/");
        nodePath.remove(1);
        
        if (file.isDirectory()) {
            TreeNode<WBSNode> node = new TreeNode<>(new WorkPackage(file.getName(), nodePath.join("/")));
            
            for (File child : file.listFiles()) {
                node.addChild(buildWBS(child, path + "/" + child.getName(), project));
            }
            
            return node;
        }
        
        try (Scanner reader = new Scanner(file)) {
            double cost = 0;
            int duration = 0;
            String description = "";
            boolean sw = false;
            LinkedList<String> dependencies = new LinkedList();
            
            while (reader.hasNextLine()) {
                if (sw) {
                    description += reader.nextLine();
                    continue;
                }
                
                String[] line = reader.nextLine().split("=");
                
                if (line.length == 2) {
                    if (line[0].equals("duration"))
                        duration = Integer.parseInt(line[1]);
                    
                    if (line[0].equals("cost"))
                        cost = Double.parseDouble(line[1]);
                    
                    if (line[0].equals("dependencies"))
                        dependencies = LinkedList.split(line[1], ",");
                    
                    if (line[0].equals("description")) {
                        sw = true;
                        description = line[1];
                    }
                }
            }
            
            Deliverable deliverable = new Deliverable(FileHelpers.getBaseName(file.getName()), nodePath.join("/"), description, cost, duration, dependencies, project.getStart());
            TreeNode<WBSNode> node = new TreeNode<>(deliverable);
            
            return node;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
