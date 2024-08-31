No desenvolvimento de sistemas muitas vezes nos deparamos com a necessidade de modelar objetos ou conceitos que possuem estados bem definidos e suas transições. 
Contudo essa definição de estado e suas transições acabam sendo desenvolvidas de forma descentralizada, ou seja, as mudanças de estados ocorrem de forma errada no sistema, 
dificultando o entendimento do fluxo de mudança de estados do objeto e, até mesmo, de evoluções, seja com a inclusão ou remoção de um novo estado,
seja com a alteração de um fluxo já existente.

Uma solução para este problema é o uso de máquinas de estados finita (FSM — Finite State Machine) que também é implementada pelo Spring Framework: Spring State Machine.

Neste exemplo, foi desenvolvido uma máquina de estados simplificada para representar o fluxo de estados de um pedido de compras:

![image](https://github.com/user-attachments/assets/519e9d4a-e259-493e-a642-a73b57270910)
