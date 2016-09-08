/*
 * GlobalTablist - get the global tablist back
 *
 * Copyright (C) 2014 Florian Stober
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package codecrafter47.globaltablist;

import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.tab.TabList;

public class TabListListener implements Listener {

    private final GlobalTablist plugin;

    public TabListListener(GlobalTablist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent e) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        if (plugin.getConfig().useGlobalTablist) {
            ProxiedPlayer player = e.getPlayer();
            //if (player.hasPermission("*"))
            //	return;

            TabList tablistHandler;
            if(player.getPendingConnection().getVersion() < 47){
                tablistHandler = new GlobalTablistHandler17(player, plugin);
            } else {
                tablistHandler = new GlobalTablistHandler18(player, plugin);
            }
            ReflectionUtil.setTablistHandler(player, tablistHandler);
            tablistHandler.onConnect();
        }
    }
    
    @EventHandler(priority = Byte.MAX_VALUE)
    public void onProxyPing(ProxyPingEvent e) {
    	
    	int hidden = 0;
    	ArrayList<ServerPing.PlayerInfo> online = new ArrayList<ServerPing.PlayerInfo>();
    	
    	for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
    		if (pp.hasPermission(plugin.getConfig().hideFromTabListPermission)) {
    			hidden += 1;
    		}
    		else if (online.size() < 10) {
    			online.add(new ServerPing.PlayerInfo(pp.getDisplayName(), pp.getUniqueId()));
    		}
    	}
    	if (hidden > 0) {
	    	e.getResponse().getPlayers().setOnline(
	    			Math.max(0, e.getResponse().getPlayers().getOnline() - hidden)
	    			);
    	}
    	
    	ServerPing.PlayerInfo[] arrOnline = new ServerPing.PlayerInfo[online.size()];
    	arrOnline = online.toArray(arrOnline);
    	e.getResponse().getPlayers().setSample(arrOnline);
    }
}
