package com.state.machine.config;

import static java.util.Objects.isNull;

import com.state.machine.events.OrderEvents;
import com.state.machine.states.OrderStates;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states.withStates()
                .initial(OrderStates.CREATED)
                .states(EnumSet.allOf(OrderStates.class))
                .end(OrderStates.COMPLETED)
                .end(OrderStates.CANCELLED);
    }

    // Para definir as transições, a configuração a seguir utiliza a seguinte sequência lógica: dado um estado de origem (source),
    // deseja-se mudar para o estado de destino (target) quando ocorrer um determinado evento (event).
    // Por exemplo, para ocorrer a transição de estados entre VALIDATED (source) para CREATED (target),
    // o evento CREATE(event) deve ocorrer.
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStates.CREATED).target(OrderStates.CREATED).event(OrderEvents.CREATE)
                .action(createOrderAction())
                .and()
                .withExternal().source(OrderStates.CREATED).target(OrderStates.SHIPPED).event(OrderEvents.SHIP)
                .action(shipOrderAction())
                .and()
                .withExternal().source(OrderStates.CREATED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .action(cancelOrderAction())
                .and()
                .withExternal().source(OrderStates.SHIPPED).target(OrderStates.DELIVERED).event(OrderEvents.DELIVER)
                .action(deliverOrderAction())
                .and()
                .withExternal().source(OrderStates.SHIPPED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .action(cancelOrderAction())
                .and()
                .withExternal().source(OrderStates.DELIVERED).target(OrderStates.PAID).event(OrderEvents.PAY)
                .action(payOrderAction())
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.COMPLETED).event(OrderEvents.COMPLETE)
                .action(completeOrderAction());
    }

    public Action<OrderStates, OrderEvents> completeOrderAction() {
        return context -> log.info("Action: Completing order");
    }

    public Action<OrderStates, OrderEvents> deliverOrderAction() {
        return context -> log.info("Action: Delivering order");
    }

    public Action<OrderStates, OrderEvents> shipOrderAction() {
        return context -> log.info("Action: Shipping order");
    }

    public Action<OrderStates, OrderEvents> cancelOrderAction() {
        return context -> log.info("Action: Cancelling order");

    }

    public Action<OrderStates, OrderEvents> payOrderAction() {
        return context -> log.info("Action: Paying for order");
    }

    public Action<OrderStates, OrderEvents> createOrderAction() {
        return context -> {
            log.info("Action: Validating payment and origin");
            // throw new RuntimeException("Invalid payment method");
        };
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {
        StateMachineListenerAdapter<OrderStates, OrderEvents> adapter =
                new StateMachineListenerAdapter<>() {
                    @Override
                    public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
                        if (isNull(from)) {
                            log.info("Initial state");
                        } else {
                            log.info("Transitioned from {} to {}", from.getId(), to.getId());
                        }
                    }
                };
        config.withConfiguration().listener(adapter);
    }
}
