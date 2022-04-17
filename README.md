# SimpleCloud v3 [WIP - Not usable]

![68747470733a2f2f692e696d6775722e636f6d2f6554514a3149582e706e67.png](https://res.craft.do/user/full/f60f4198-081b-3e55-baec-a8033b92a100/doc/FD9B970E-06AD-49CF-8AAE-FC89C7018F5C/F0F4A881-7C18-4A24-8B78-D8C7D29256CF_2/zHW7KupxA6alA2cxvPh4QZTMSZ3cEr0UeOt7zydxbIcz/68747470733a2f2f692e696d6775722e636f6d2f6554514a3149582e706e67.png)

## What is SimpleCloud?

### What is SimpleCloud 3?

SimpleCloud is a software for administrating a minecraft server network. It manages your templates, players, permissions, groups and servers in a kubernetes cluster which automatically makes it highly available. And everything can be controlled by using the built-in dashboard.

### Differences to SimpleCloud 2

The biggest differnce to SimpleCloud 2 is that SimpleCloud 3 runs only in a **kubernetes cluster**. But this brings a lot of benefits for you. For example, it’s much easier to scale SimpleCloud up and down as you like using multiple nodes. And as soon as you have 3 nodes, SimpleCloud automatically becomes **highly available**. Furthermore SimpleCloud 3 no longer provides a command line and everything can be controlled using the **dashboard** or **the Rest-API**.

## Why is SimpleCloud 3 not usable yet?

In the last few months we have come very far with SimpleCloud but we are **not done** yet. We have decided to publish it anyway, so you can see **progress**, **contribute** and give us **feedback**.

Those things are missing to make it usable:

- Dashboard
- Plugin loading (Formerly known as Modules)
- The Ingame-Plugin for communicating with the node
- Build Docker Images with Kubernetes form templates

## Dashboard

SimpleCloud will release with a **built-in dashboard**. At the moment it is still work-in-progress and not available publicly.

## Plugins (Formerly known as Modules)

We are planning to release following plugins:

- [ ] Permissions
- [ ] Signs
- [ ] Proxy Management
- [ ] NPCs
- [ ] Chat + Tab
- [ ] Hub Command
- [ ] Notify

## Contributing

If you are interested in contributing you can start by reading our [Contributing-Guidelines](CONTRIBUTING.md).
