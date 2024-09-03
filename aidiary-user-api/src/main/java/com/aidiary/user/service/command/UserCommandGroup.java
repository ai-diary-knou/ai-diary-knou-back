package com.aidiary.user.service.command;

import java.util.ArrayList;
import java.util.List;

public class UserCommandGroup implements UserCommand{

    private List<UserCommand> commands = new ArrayList<>();

    public void add(UserCommand userCommand) {
        commands.add(userCommand);
    }

    @Override
    public void execute(UserCommandContext context) {
        for (UserCommand userCommand : commands) {
            userCommand.execute(context);
        }
    }
}
