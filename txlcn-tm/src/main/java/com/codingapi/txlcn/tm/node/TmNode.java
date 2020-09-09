package com.codingapi.txlcn.tm.node;

import com.codingapi.txlcn.protocol.ProtocolServer;
import com.codingapi.txlcn.tm.repository.redis.RedisTmNodeRepository;
import com.codingapi.txlcn.tm.util.NetUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codingapi.txlcn.tm.constant.CommonConstant.TX_MANAGE_KEY;

/**
 * @author WhomHim
 * @description TM 的 p2p 单节点
 * @date Create in 2020/9/3 17:22
 */
@Data
@Accessors(chain = true)
public class TmNode {

    /**
     * tm 的全局唯一 Id : ip 加端口
     */
    private String id;

    /**
     * Tm node 节点的 IP
     */
    private String nodeIp;

    /**
     * 端口
     */
    private int port;

    private RedisTmNodeRepository redisTmNodeRepository;

    public TmNode(String hostAndPort, String nodeIp, int port, RedisTmNodeRepository redisTmNodeRepository) {
        this.id = hostAndPort;
        this.nodeIp = nodeIp;
        this.port = port;
        this.redisTmNodeRepository = redisTmNodeRepository;
    }

    /**
     * @return 获得除此 TM 节点以外 TM 节点的 IP 及端口
     */
    public List<InetSocketAddress> getOtherNodeList() {
        return redisTmNodeRepository.keys(TX_MANAGE_KEY).stream()
                .filter(Objects::nonNull)
                .map(tmKey -> redisTmNodeRepository.getTmNodeAddress(tmKey))
                .filter(s -> !s.equals(id))
                .map(NetUtil::addressFormat)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * @param iNetSocketAddressList iNetSocketAddressList
     * @return 获得传入集合以外的 TM 节点的 IP 及端口
     */
    public List<InetSocketAddress> getBesidesNodeList(List<InetSocketAddress> iNetSocketAddressList) {
        return redisTmNodeRepository.keys(TX_MANAGE_KEY).stream()
                .filter(Objects::nonNull)
                .map(tmKey -> redisTmNodeRepository.getTmNodeAddress(tmKey))
                .filter(s -> !s.equals(id))
                .map(NetUtil::addressFormat)
                .filter(Objects::nonNull)
                .filter(iNetSocketAddress -> !iNetSocketAddressList.contains(iNetSocketAddress))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 连接除此 TM 节点以外 TM 节点
     */
    public void connectToOtherNode(ProtocolServer protocolServer) {
        List<InetSocketAddress> otherNodeList = this.getOtherNodeList();
        otherNodeList.forEach(iNetSocketAddress ->
                protocolServer.connectTo(iNetSocketAddress.getHostString(), iNetSocketAddress.getPort()));
    }

    public void sentToOtherNode(){

    }
}


