/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.images.Images;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.gwtext.client.widgets.tree.TreeNode;



public class PackagesTree extends AbstractTree {
    private static Constants constants = GWT.create(Constants.class);
    private static Images images = (Images) GWT.create(Images.class);       

    private Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();

    private boolean packagesLoaded = false;

    public PackagesTree(ExplorerViewCenterPanel tabbedPanel) {
        super(tabbedPanel);
        this.name = constants.KnowledgeBases();
        this.image = images.packages();


        //these panels are lazy loaded to easy startup wait time.
/*        addListener(new PanelListenerAdapter() {
        	public void onExpand(Panel panel) {
                loadPackageList();
            }
        });

        add(packagesPanel);
        */
        mainTree = packageExplorer(tabbedPanel);
        mainTree.addSelectionHandler(this);

    }
/*
    public void loadPackageList() {
        if (!packagesLoaded) {
            packagesPanel.add(packageExplorer(centertabbedPanel));
            packagesLoaded = true;
        }
    }
*/
    private void refreshPackageTree() {
    	//TODO (JLIU):
        //packagesPanel.remove(1);
        //packagesPanel.add(packageExplorer(centertabbedPanel));
    }

    private Tree packageExplorer(final ExplorerViewCenterPanel tabPanel) {
    	Tree rootNode = new Tree();    	
    	rootNode.setAnimationEnabled(true);
 
    	TreeItem packageRootNode = rootNode.addItem(Util.getHeader(images.chartOrganisation(), constants.Packages()));
        packageRootNode.setState(true);
		loadPackages(packageRootNode, itemWidgets);
		rootNode.addItem(packageRootNode);

		loadGlobal(rootNode, itemWidgets);

/*            @Override
            public void onCollapseNode(final TreeItem node) {
                if (node.getText().equals(constants.Packages())) {
                    Node[] children = node.getChildNodes();
                    for (Node child : children) {
                        node.removeChild(child);
                    }
                    loadPackages(node, itemWidgets);
                }
            }*/

        return rootNode;
    }

    private void loadPackages(final TreeItem root, final Map<TreeItem, String> itemWidgets) {
        RepositoryServiceFactory.getService().listPackages(
                new GenericCallback<PackageConfigData[]>() {
                    public void onSuccess(PackageConfigData[] value) {
                        PackageHierarchy ph = new PackageHierarchy();

                        for (PackageConfigData val : value) {
                            ph.addPackage(val);
                        }

                        for (PackageHierarchy.Folder hf : ph.root.children) {
                            buildPkgTree(root, hf);
                        }

                        //root.expand();
                    }
                });
    }

    private void loadGlobal(final Tree root, final Map<TreeItem, String> itemWidgets) {
        RepositoryServiceFactory.getService().loadGlobalPackage(
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess(PackageConfigData value) {
                    	TreeItem globalRootNode = ExplorerNodeConfig.getPackageItemStructure(constants.GlobalArea(), value.uuid, itemWidgets);
                    	globalRootNode.setHTML(Util.getHeader(images.chartOrganisation(), constants.GlobalArea()));
                    	globalRootNode.setUserObject(value);
                        		
                        root.addItem(globalRootNode);
                    }
                });
    }
    
    private void buildPkgTree(TreeItem root, PackageHierarchy.Folder fldr) {
        if (fldr.conf != null) {
            root.addItem(loadPackage(fldr.name, fldr.conf));
        } else {        	
            TreeItem tn = new TreeItem(Util.getHeader(images.emptyPackage(), fldr.name));
            //itemWidgets.put(item, AssetFormats.BUSINESS_RULE_FORMATS[0]);
            root.addItem(tn);

            for (PackageHierarchy.Folder c : fldr.children) {
                buildPkgTree(tn, c);
            }
        }
    }

    private TreeItem loadPackage(String name, PackageConfigData conf) {
    	TreeItem pn = ExplorerNodeConfig.getPackageItemStructure(name, conf.uuid, itemWidgets);
        pn.setUserObject(conf);
        return pn;
    }

    public static String key(String[] fmts, PackageConfigData userObject) {
        String key = userObject.uuid;
        for (String fmt : fmts) {
            key = key + fmt;
        }
        if (fmts.length == 0) {
            key = key + "[0]";
        }
        return key;
    }
    

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem node = event.getSelectedItem();
        //String widgetID = itemWidgets.get(node);
         
/*        //this refreshes the list.
        if (content.equals(ExplorerNodeConfig.CATEGORY_ID)) { 
            //self.getParentNode().replaceChild(ExplorerNodeConfig.getCategoriesStructure(), self);
        } else if (content.equals(ExplorerNodeConfig.STATES_ID)) {   
            //self.getParentNode().replaceChild(ExplorerNodeConfig.getStatesStructure(), self);
        } else */
        	
		if (node.getUserObject() instanceof PackageConfigData
				&& !"global".equals(((PackageConfigData) node.getUserObject()).name)) {
			PackageConfigData pc = (PackageConfigData) node.getUserObject();
			RulePackageSelector.currentlySelectedPackage = pc.name;

			String uuid = pc.uuid;
			centertabbedPanel.openPackageEditor(uuid, new Command() {
				public void execute() {
					// refresh the package tree.
					refreshPackageTree();
				}
			});
		} else if (node.getUserObject() instanceof Object[]) {
			//Object[] uo = (Object[]) node.getUserObject();
			//final String[] fmts = (String[]) uo[0];
			final String[] fmts = (String[]) node.getUserObject();
			final PackageConfigData pc = (PackageConfigData) node.getParentItem().getUserObject();
			RulePackageSelector.currentlySelectedPackage = pc.name;
			String key = key(fmts, pc);
			if (!centertabbedPanel.showIfOpen(key)) {

				final AssetItemGrid list = new AssetItemGrid(new EditItemEvent() {
					public void open(String uuid) {
						centertabbedPanel.openAsset(uuid);
					}

					public void open(MultiViewRow[] rows) {
						centertabbedPanel.openAssets(rows);
					}
				}, AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID, new AssetItemGridDataLoader() {
					public void loadData(int startRow, int numberOfRows, GenericCallback<TableDataResult> cb) {
						RepositoryServiceFactory.getService().listAssets(pc.uuid, fmts, startRow, numberOfRows,
								AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID, cb);
					}
				}, GWT.getModuleBaseURL() + "feed/package?name=" + pc.name + "&viewUrl="
						+ CategoriesPanel.getSelfURL() + "&status=*");
				centertabbedPanel.addTab(node.getText() + " [" + pc.name + "]", list, key);

				final ServerPushNotification sub = new ServerPushNotification() {
					public void messageReceived(PushResponse response) {
						if (response.messageType.equals("packageChange") && response.message.equals(pc.name)) {
							list.refreshGrid();
						}
					}
				};
				PushClient.instance().subscribe(sub);
				list.addUnloadListener(new Command() {
					public void execute() {
						PushClient.instance().unsubscribe(sub);
					}
				});
			}
		}

    }  
    
}