package com.state.machine.config;

import com.state.machine.entity.Order;
import com.state.machine.events.OrderEvents;
import com.state.machine.repository.OrderRepository;
import com.state.machine.service.OrderService;
import com.state.machine.states.OrderStates;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderStateMachineInterceptor extends StateMachineInterceptorAdapter<OrderStates, OrderEvents> {

    private final OrderRepository orderRepository;

    @Override
    public void preStateChange(State<OrderStates, OrderEvents> state, Message<OrderEvents> message, Transition<OrderStates, OrderEvents> transition, StateMachine<OrderStates, OrderEvents> stateMachine, StateMachine<OrderStates, OrderEvents> rootStateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(OrderService.ORDER_ID_HEADER, -1L)))
                .ifPresent(orderId -> {
                    if(orderId == -1L) {
                        orderRepository.save(new Order(state.getId()));
                        return;
                    }
                    final var order = orderRepository.findById(orderId).get();
                    order.setStatus(state.getId());
                    orderRepository.save(order);
                }));
    }
}
