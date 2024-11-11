package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * {@link MultiPaneComponentModown} implementation for {@link ISkEvent}.
 *
 * @author Slavage
 */
public class SkEventM5Mpc
    extends MultiPaneComponentModown<SkEvent> {

  static final ITsNodeKind<String>  NK_ALARM = new TsNodeKind<>( "LeafSkAlarm", String.class, true );   //$NON-NLS-1$
  static final ITsNodeKind<SkEvent> NK_EVENT = new TsNodeKind<>( "LeafSkEvent", SkEvent.class, false ); //$NON-NLS-1$

  class CacheNode {

    ITsNode                     node;
    IMapEdit<String, CacheNode> childs = new ElemMap<String, CacheNode>();

    CacheNode( ITsNode aNode ) {
      node = aNode;
    }

    CacheNode findChild( String aNodeName ) {
      return childs.findByKey( aNodeName );
    }

    void addChild( String aNodeName, CacheNode aNode ) {
      childs.put( aNodeName, aNode );
    }
  }

  class TreeMakerByAlarm
      implements ITsTreeMaker<SkEvent> {

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<SkEvent> aEvents ) {
      IStringMapEdit<DefaultTsNode<String>> roots = new StringMap<>();

      CacheNode rootCacheNode = new CacheNode( aRootNode );

      for( SkEvent event : aEvents ) {
        ISkAlarm alarm = skConn().coreApi().objService().find( event.eventGwid().skid() );

        // Grouping by alarm.
        CacheNode alarmCacheNode = rootCacheNode.findChild( alarm.description() );
        if( alarmCacheNode == null ) {
          DefaultTsNode<String> alarmNode = new DefaultTsNode<>( NK_ALARM, aRootNode, alarm.description() );
          ((DefaultTsNode<String>)aRootNode).addNode( alarmNode );
          roots.put( alarm.description(), alarmNode );

          alarmCacheNode = new CacheNode( alarmNode );
          rootCacheNode.addChild( alarm.description(), alarmCacheNode );
        }

        DefaultTsNode<SkEvent> eventNode = new DefaultTsNode<SkEvent>( NK_EVENT, alarmCacheNode.node, event );
      }
      return (IList)roots.values();
    }

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return aNode.kind() == NK_EVENT;
    }
  }

  public SkEventM5Mpc( ITsGuiContext aContext, IM5Model<SkEvent> aModel, IM5ItemsProvider<SkEvent> aItemsProvider,
      IM5LifecycleManager<SkEvent> aLifecycleManager ) {
    super( aContext, aModel, aItemsProvider, aLifecycleManager );

    TreeModeInfo<SkEvent> tmiByAlarm = new TreeModeInfo<>( "ByAlarm", //$NON-NLS-1$
        "По тревогам", "Тревоги", "ByAlarm", new TreeMakerByAlarm() );
    treeModeManager().addTreeMode( tmiByAlarm );
  }

  @Override
  protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
      IListEdit<ITsActionDef> aActs ) {
    return super.doCreateToolbar( aContext, aName, aIconSize, aActs );
  }

  @Override
  protected void doProcessAction( String aActionId ) {
  }

  @Override
  protected void doUpdateActionsState( boolean aIsAlive, boolean aIsSel, SkEvent aSel ) {
  }

  // ------------------------------------------------------------------------------------
  // Implementation
  //

  public ISkConnection skConn() {
    return model().domain().tsContext().get( ISkConnection.class );
  }

}
