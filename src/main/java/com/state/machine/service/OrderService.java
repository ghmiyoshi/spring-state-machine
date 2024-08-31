package com.state.machine.service;

import static java.util.Objects.isNull;
import static org.springframework.statemachine.StateMachineEventResult.ResultType.DENIED;

import com.state.machine.config.OrderStateMachineInterceptor;
import com.state.machine.entity.Order;
import com.state.machine.events.OrderEvents;
import com.state.machine.repository.OrderRepository;
import com.state.machine.states.OrderStates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    public static final String ORDER_ID_HEADER = "order_id";

    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;
    private final OrderStateMachineInterceptor orderStateMachineInterceptor;

    public void createOrder(Order order) {
        log.info("Event: Creating order");
        sendEvent(order.getId(), build(order.getId()),
                OrderEvents.CREATE);
    }

    public void payOrder(Long orderId) {
        log.info("Event: Paying order");
        sendEvent(orderId, build(orderId), OrderEvents.PAY);
    }

    public void shipOrder(Long orderId) {
        log.info("Event: Shiping order");
        sendEvent(orderId, build(orderId), OrderEvents.SHIP);
    }

    public void deliverOrder(Long orderId) {
        log.info("Event: Delivering order");
        sendEvent(orderId, build(orderId), OrderEvents.DELIVER);
    }

    public void completeOrder(Long orderId) {
        log.info("Event: Completing order");
        sendEvent(orderId, build(orderId), OrderEvents.COMPLETE);
    }

    public void cancelOrder(Long orderId) {
        log.info("Event: Canceling order");
        sendEvent(orderId, build(orderId), OrderEvents.CANCEL);
    }

    private void sendEvent(Long orderId, StateMachine<OrderStates, OrderEvents> stateMachine,
                           OrderEvents event) {
        Message<OrderEvents> message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();

        stateMachine.sendEvent(Mono.just(message))
                .subscribe(result -> handleEventResult(result, event, stateMachine));
    }

    private void handleEventResult(StateMachineEventResult<OrderStates, OrderEvents> result,
                                   OrderEvents event, StateMachine<OrderStates, OrderEvents> stateMachine) {
        log.info("State: {}", result.getResultType());
        if (DENIED.equals(result.getResultType())) {
            log.info("Transition denied for event: {} in state: {}",
                    event, stateMachine.getState().getId());
        }
    }

    private StateMachine<OrderStates, OrderEvents> build(Long orderId) {
        StateMachine<OrderStates, OrderEvents> stateMachine;
        if (isNull(orderId)) {
            stateMachine = stateMachineFactory.getStateMachine();
            configureStateMachine(stateMachine, OrderStates.CREATED);
        } else {
            var order = orderRepository.findById(orderId).orElseThrow(
                    () -> new IllegalArgumentException("Order not found"));
            stateMachine = stateMachineFactory.getStateMachine(orderId.toString());
            configureStateMachine(stateMachine, order.getStatus());
        }
        return stateMachine;
    }

    private void configureStateMachine(StateMachine<OrderStates, OrderEvents> stateMachine,
                                       OrderStates initialState) {
        stateMachine.stopReactively().block();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(stateMachineAccessor -> {
                    stateMachineAccessor.addStateMachineInterceptor(orderStateMachineInterceptor);
                    stateMachineAccessor.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(initialState,
                                    null, null, null)).block();
                });
        stateMachine.startReactively().block();
    }
}
