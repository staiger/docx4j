package org.docx4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.docx4j.dml.picture.Pic;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.CTSdtContentRow;

/**
 * Traverse a list of JAXB objects (eg document.xml's document/body 
 * children), and do something to them.
 * 
 * This is similar to what one could do via XSLT,
 * but avoids marshalling/unmarshalling.  The downside is that
 * not everything will necessarily get traversed here
 * since visitChildren is not (yet) comprehensive.
 * 
 * @author jharrop
 *
 */
public class TraversalUtil {

	private static Logger log = Logger.getLogger(TraversalUtil.class);

	public interface Callback {

		void walkJAXBElements(Object parent);

		List<Object> getChildren(Object o);

		/**
		 * Visits a node in pre order (before its children have been visited).
		 * 
		 * A node is visited only if all its parents have been traversed (
		 * {@link #shouldTraverse(Object)}).</p>
		 * 
		 * <p>
		 * Implementations can have side effects.
		 * </p>
		 */
		List<Object> apply(Object o);

		/**
		 * Decide whether this node's children should be traversed.
		 * 
		 * @return whether the children of this node should be visited
		 */
		boolean shouldTraverse(Object o);

	}

	Callback cb;

	public TraversalUtil(Object parent, Callback cb) {

		this.cb = cb;
		cb.walkJAXBElements(parent);
	}

	static void visitChildrenImpl(Object o) {

	}

	public static List<Object> getChildrenImpl(Object o) {

		if (o instanceof org.docx4j.wml.Body) {

			return ((org.docx4j.wml.Body) o).getEGBlockLevelElts();

		} else if (o instanceof org.docx4j.wml.SdtBlock) {

			return ((org.docx4j.wml.SdtBlock) o).getSdtContent()
					.getEGContentBlockContent();

		} else if (o instanceof org.docx4j.wml.P) {

			return ((org.docx4j.wml.P) o).getParagraphContent();

		} else if (o instanceof org.docx4j.wml.R) {

			return ((org.docx4j.wml.R) o).getRunContent();

		} else if (o instanceof org.docx4j.wml.CTSdtContentRow) {
			return ((org.docx4j.wml.CTSdtContentRow) o)
					.getEGContentRowContent();
		} else if (o instanceof org.docx4j.wml.SdtContentBlock) {
			return ((org.docx4j.wml.SdtContentBlock) o)
					.getEGContentBlockContent();
		} else if (o instanceof org.docx4j.wml.CTSdtContentRun) {
			return ((org.docx4j.wml.CTSdtContentRun) o).getParagraphContent();

		} else if (o instanceof org.docx4j.wml.SdtRun) {

			return ((org.docx4j.wml.SdtRun) o).getSdtContent()
					.getParagraphContent();

		} else if (o instanceof org.docx4j.wml.CTSdtRow) {

			return ((org.docx4j.wml.CTSdtRow) o).getSdtContent()
					.getEGContentRowContent();

		} else if (o instanceof org.docx4j.wml.Tbl) {

			// Could get the TblPr if we wanted them
			// org.docx4j.wml.TblPr tblPr = tbl.getTblPr();

			// Could get the TblGrid if we wanted it
			// org.docx4j.wml.TblGrid tblGrid = tbl.getTblGrid();

			return ((org.docx4j.wml.Tbl) o).getEGContentRowContent();

		} else if (o instanceof org.docx4j.wml.Tr) {

			return ((org.docx4j.wml.Tr) o).getEGContentCellContent();

		} else if (o instanceof org.docx4j.wml.Tc) {

			return ((org.docx4j.wml.Tc) o).getEGBlockLevelElts();

		} else {

			if (o instanceof org.w3c.dom.Node) {
				log.warn(" IGNORED " + ((org.w3c.dom.Node) o).getNodeName());
			} else {
//				log.warn(" IGNORED " + o.getClass().getName());
			}

		}
		return null;
	}

	public static void main(String[] args) throws Exception {

		String inputfilepath = System.getProperty("user.dir")
				+ "/sample-docs/sample-docx.xml";

		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
				.load(new java.io.File(inputfilepath));
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

		org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) documentPart
				.getJaxbElement();
		Body body = wmlDocumentEl.getBody();

		new TraversalUtil(body,

		new Callback() {

			String indent = "";
			
			@Override
			public List<Object> apply(Object o) {
				
				String text = "";
				if (o instanceof org.docx4j.wml.Text)
					text = ((org.docx4j.wml.Text)o).getValue();
				
				System.out.println(indent + o.getClass().getName() + "  \"" + text + "\"");
				return null;
			}

			@Override
			public boolean shouldTraverse(Object o) {
				return true;
			}

			// Depth first
			@Override
			public void walkJAXBElements(Object parent) {

				indent += "    ";
				
				List children = getChildren(parent);
				if (children != null) {

					for (Object o : children) {

						// if its wrapped in javax.xml.bind.JAXBElement, get its
						// value; this is ok, provided the results of the Callback
						// won't be marshalled
						o = XmlUtils.unwrap(o);

						this.apply(o);

						if (this.shouldTraverse(o)) {
							walkJAXBElements(o);
						}

					}
				}
				
				indent = indent.substring(0, indent.length()-4);
			}

			@Override
			public List<Object> getChildren(Object o) {
				return TraversalUtil.getChildrenImpl(o);
			}

		}

		);

	}

	public static void replaceChildren(Object o, List<Object> newChildren) {
		
		// Available if you need something like this.  Would be used with
		// something like:
		
		/*
		 * 	  public void walkJAXBElements(Object parent){
		  // Breadth first
				
			  List<Object> newChildren = new ArrayList<Object>(); 
			  
				List children = getChildren(parent);
				if (children==null) {
					log.warn("no children: " + parent.getClass().getName() );
				} else {
					for (Object o : getChildren(parent) ) {

						// if its wrapped in javax.xml.bind.JAXBElement, get its value
						o = XmlUtils.unwrap(o);
						
						newChildren.addAll(this.apply(o));
					}
				}				
				// Replace list, so we'll traverse all the new sdts we've just created
				replaceChildren(parent, newChildren);
				
				
				for (Object o : getChildren(parent) ) {
					
					this.apply(o);
					
					if ( this.shouldTraverse(o) ) {
						walkJAXBElements(o);
					}
					
				}
				
			}

		 */

		if (o instanceof org.docx4j.wml.SdtBlock) {

			((org.docx4j.wml.SdtBlock) o).getSdtContent()
					.getEGContentBlockContent().clear();
			((org.docx4j.wml.SdtBlock) o).getSdtContent()
					.getEGContentBlockContent().addAll(newChildren);

		} else if (o instanceof org.docx4j.wml.Body) {

			((org.docx4j.wml.Body) o).getEGBlockLevelElts().clear();
			((org.docx4j.wml.Body) o).getEGBlockLevelElts().addAll(newChildren);
			
		} else if (o instanceof org.docx4j.wml.P) {

			((org.docx4j.wml.P) o).getParagraphContent().clear();
			((org.docx4j.wml.P) o).getParagraphContent().addAll(newChildren);

		} else if (o instanceof org.docx4j.wml.R) {

			((org.docx4j.wml.R) o).getRunContent().clear();
			((org.docx4j.wml.R) o).getRunContent().addAll(newChildren);

		} else if (o instanceof org.docx4j.wml.CTSdtContentRow) {
			((org.docx4j.wml.CTSdtContentRow) o).getEGContentRowContent()
					.clear();
			((org.docx4j.wml.CTSdtContentRow) o).getEGContentRowContent()
					.addAll(newChildren);
		} else if (o instanceof org.docx4j.wml.SdtContentBlock) {
			((org.docx4j.wml.SdtContentBlock) o).getEGContentBlockContent()
					.clear();
			((org.docx4j.wml.SdtContentBlock) o).getEGContentBlockContent()
					.addAll(newChildren);
		} else if (o instanceof org.docx4j.wml.CTSdtContentRun) {
			((org.docx4j.wml.CTSdtContentRun) o).getParagraphContent().clear();
			((org.docx4j.wml.CTSdtContentRun) o).getParagraphContent().addAll(
					newChildren);

		} else if (o instanceof org.docx4j.wml.SdtRun) {

			((org.docx4j.wml.SdtRun) o).getSdtContent().getParagraphContent()
					.clear();
			((org.docx4j.wml.SdtRun) o).getSdtContent().getParagraphContent()
					.addAll(newChildren);

		} else if (o instanceof org.docx4j.wml.CTSdtRow) {

			((org.docx4j.wml.CTSdtRow) o).getSdtContent()
					.getEGContentRowContent().clear();
			((org.docx4j.wml.CTSdtRow) o).getSdtContent()
					.getEGContentRowContent().addAll(newChildren);

		} else if (o instanceof org.docx4j.wml.Tbl) {

			((org.docx4j.wml.Tbl) o).getEGContentRowContent().clear();
			((org.docx4j.wml.Tbl) o).getEGContentRowContent().addAll(
					newChildren);

		} else if (o instanceof org.docx4j.wml.Tr) {

			((org.docx4j.wml.Tr) o).getEGContentCellContent().clear();
			((org.docx4j.wml.Tr) o).getEGContentCellContent().addAll(
					newChildren);

		} else if (o instanceof org.docx4j.wml.Tc) {

			((org.docx4j.wml.Tc) o).getEGBlockLevelElts().clear();
			((org.docx4j.wml.Tc) o).getEGBlockLevelElts().addAll(newChildren);

		} else {

			if (o instanceof org.w3c.dom.Node) {
				log.warn(" IGNORED " + ((org.w3c.dom.Node) o).getNodeName());
			} else {
//				log.warn(" IGNORED " + o.getClass().getName());
			}

		}
	}
	// private void describeDrawing( org.docx4j.wml.Drawing d) {
	//			
	// log.info("In wml.Drawing" );
	//			
	// if ( d.getAnchorOrInline().get(0) instanceof Anchor ) {
	//				
	// log.info(" ENCOUNTERED w:drawing/wp:anchor " );
	// // That's all for now...
	//				
	// } else if ( d.getAnchorOrInline().get(0) instanceof Inline ) {
	//				
	// // Extract
	// w:drawing/wp:inline/a:graphic/a:graphicData/pic:pic/pic:blipFill/a:blip/@r:embed
	//				
	// Inline inline = (Inline )d.getAnchorOrInline().get(0);
	//				
	// Pic pic = inline.getGraphic().getGraphicData().getPic();
	//					
	// if (pic!=null) {
	// log.info("image relationship: " + pic.getBlipFill().getBlip().getEmbed()
	// );
	// }
	//				
	//				
	// } else {
	//				
	// log.info(" Didn't get Inline :(  How to handle " +
	// d.getAnchorOrInline().get(0).getClass().getName() );
	// }
	//			
	// }

}