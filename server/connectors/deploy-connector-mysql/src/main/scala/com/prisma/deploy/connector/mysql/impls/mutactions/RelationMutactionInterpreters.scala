package com.prisma.deploy.connector.mysql.impls.mutactions

import com.prisma.deploy.connector.mysql.database.DeployDatabaseMutationBuilderMySql
import com.prisma.deploy.connector.{CreateRelationTable, DeleteRelationTable}

object CreateRelationInterpreter extends SqlMutactionInterpreter[CreateRelationTable] {
  override def execute(mutaction: CreateRelationTable) = {
    val aModel = mutaction.schema.getModelById_!(mutaction.relation.modelAId)
    val bModel = mutaction.schema.getModelById_!(mutaction.relation.modelBId)

    DeployDatabaseMutationBuilderMySql.createRelationTable(
      projectId = mutaction.projectId,
      tableName = mutaction.relation.relationTableName,
      aTableName = aModel.name,
      bTableName = bModel.name
    )
  }

  override def rollback(mutaction: CreateRelationTable) = {
    DeployDatabaseMutationBuilderMySql.dropTable(projectId = mutaction.projectId, tableName = mutaction.relation.relationTableName)
  }
}

object DeleteRelationInterpreter extends SqlMutactionInterpreter[DeleteRelationTable] {
  override def execute(mutaction: DeleteRelationTable) = {
    DeployDatabaseMutationBuilderMySql.dropTable(projectId = mutaction.projectId, tableName = mutaction.relation.relationTableName)
  }

  override def rollback(mutaction: DeleteRelationTable) = {
    val createRelation = CreateRelationTable(mutaction.projectId, mutaction.schema, mutaction.relation)
    CreateRelationInterpreter.execute(createRelation)
  }
}
